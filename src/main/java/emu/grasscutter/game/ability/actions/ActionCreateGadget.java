package emu.grasscutter.game.ability.actions;

import com.google.protobuf.*;
import emu.grasscutter.Grasscutter;
import emu.grasscutter.data.binout.AbilityModifier.AbilityModifierAction;
import emu.grasscutter.game.ability.Ability;
import emu.grasscutter.game.entity.*;
import emu.grasscutter.game.props.CampTargetType;
import emu.grasscutter.game.world.Position;
import emu.grasscutter.net.proto.AbilityActionCreateGadgetOuterClass.AbilityActionCreateGadget;
import java.util.ArrayList;

@AbilityAction(AbilityModifierAction.Type.CreateGadget)
public class ActionCreateGadget extends AbilityActionHandler {

    @Override
    public boolean execute(
            Ability ability, AbilityModifierAction action, ByteString abilityData, GameEntity target) {
        var entity = ability.getOwner();
        AbilityActionCreateGadget createGadget;
        try {
            createGadget = AbilityActionCreateGadget.parseFrom(abilityData);
        } catch (InvalidProtocolBufferException e) {
            return false;
        }

        var entityCreated =
                new EntityGadget(
                        entity.getScene(),
                        action.gadgetID,
                        new Position(createGadget.getPos()),
                        new Position(createGadget.getRot()),
                        action.campID,
                        CampTargetType.getTypeByName(action.campTargetType).getValue());
        var ownerForNew = action.ownerIsTarget ? target : entity;
        entityCreated.setOwner(ownerForNew);

        var scene = entity.getScene();

        new ArrayList<>(scene.getEntities().values()).stream()
                .filter(e -> e instanceof EntityGadget eg
                        && eg.getGadgetId() == action.gadgetID
                        && eg.getOwner() == ownerForNew)
                .forEach(e -> scene.removeEntity(e));

        scene.addEntity(entityCreated);

        return true;
    }
}
