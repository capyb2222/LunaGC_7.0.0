package emu.grasscutter.game.ability.actions;

import com.google.protobuf.*;
import emu.grasscutter.Grasscutter;
import emu.grasscutter.data.binout.AbilityModifier.AbilityModifierAction;
import emu.grasscutter.game.ability.Ability;
import emu.grasscutter.game.entity.*;
import emu.grasscutter.game.props.CampTargetType;
import emu.grasscutter.game.world.Position;
import emu.grasscutter.net.proto.AbilityActionCreateGadgetOuterClass.AbilityActionCreateGadget;

@AbilityAction(AbilityModifierAction.Type.CreateGadget)
public class ActionCreateGadget extends AbilityActionHandler {

    /**
     * Whether the client is already running - and populating - the chain this creation hangs off, in
     * which case a copy of ours is a second one the player can see.
     *
     * <p>Only chains rooted at a player's avatar count. A summon hanging off a monster is left alone:
     * the reasoning here is about what the client spawns for its own character, and an enemy's
     * mechanics are not that.
     */
    private static boolean clientOwnsChain(GameEntity entity) {
        if (entity instanceof EntityClientGadget) return true;

        // Owners are only ever set at creation, to an entity that already exists, so walking up
        // cannot come back around.
        while (entity instanceof EntityGadget summon && summon.getOwner() != null) {
            entity = summon.getOwner();
            if (entity instanceof EntityAvatar || entity instanceof EntityClientGadget) return true;
        }
        return false;
    }

    @Override
    public boolean execute(
            Ability ability, AbilityModifierAction action, ByteString abilityData, GameEntity target) {
        var entity = ability.getOwner();

        // The client owns these chains and spawns its own copies, so ours are only ever duplicates -
        // and duplicates the owner can see, since addEntity below broadcasts to everyone while a
        // client-made gadget is deliberately not echoed back to its own client.
        //
        // Two shapes of it. A gadget the client created outright, which is Odette's shadows. And a
        // summon spawning the next hop, which is Furina: her skill puts out an invisible Salon
        // Solitaire controller, and that controller's own ability creates the singers. Those pile up
        // rather than merely double, because every cast builds a fresh controller and the cleanup
        // below only recognises summons belonging to the one controller it is standing on - so the
        // previous cast's singers match nothing, and neither does the KillGadget that should retire
        // them when the skill ends.
        if (clientOwnsChain(entity)) {
            return true;
        }

        AbilityActionCreateGadget createGadget;
        try {
            createGadget = AbilityActionCreateGadget.parseFrom(abilityData);
        } catch (InvalidProtocolBufferException e) {
            return false;
        }

        // The payload only carries a position when the action came from a real create-gadget
        // invocation. Reached from a modifier being added it carries something else entirely, and
        // taking its absent pos would strand the summon at the world origin - so fall back to
        // whoever is summoning it.
        var pos =
                createGadget.hasPos() ? new Position(createGadget.getPos()) : entity.getPosition().clone();
        var rot =
                createGadget.hasRot() ? new Position(createGadget.getRot()) : entity.getRotation().clone();

        var entityCreated =
                new EntityGadget(
                        entity.getScene(),
                        action.gadgetID,
                        pos,
                        rot,
                        action.campID,
                        CampTargetType.getTypeByName(action.campTargetType).getValue());
        var owner = action.ownerIsTarget ? target : entity;
        entityCreated.setOwner(owner);

        // Nothing on this side runs the summon's own KillSelf, so without this every charged attack
        // would leave another copy standing in the scene. One summon of a given kind per owner.
        entity.getScene().getEntities().values().stream()
                .filter(e -> e instanceof EntityGadget g
                        && g.getGadgetId() == action.gadgetID
                        && g.getOwner() == owner)
                .toList()
                .forEach(stale -> entity.getScene().removeEntity(stale));

        entity.getScene().addEntity(entityCreated);

        Grasscutter.getLogger()
                .trace(
                        "Gadget {} created at pos {} rot {}",
                        action.gadgetID,
                        entityCreated.getPosition(),
                        entityCreated.getRotation());

        return true;
    }
}
