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

    @Override
    public boolean execute(
            Ability ability, AbilityModifierAction action, ByteString abilityData, GameEntity target) {
        var entity = ability.getOwner();

        // the client owns this chain and spawns its own copy, so ours would just be a duplicate
        if (entity instanceof EntityClientGadget) {
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
