package emu.grasscutter.game.ability.actions;

import com.google.protobuf.ByteString;
import emu.grasscutter.Grasscutter;
import emu.grasscutter.data.binout.AbilityModifier.AbilityModifierAction;
import emu.grasscutter.game.ability.Ability;
import emu.grasscutter.game.entity.EntityAvatar;
import emu.grasscutter.net.proto.PropChangeReasonOuterClass.PropChangeReason;
import emu.grasscutter.server.packet.send.PacketEntityFightPropUpdateNotify;
import emu.grasscutter.server.packet.send.PacketEntityFightPropChangeReasonNotify;
import emu.grasscutter.net.proto.ChangeEnergyReasonOuterClass.ChangeEnergyReason;

import emu.grasscutter.game.entity.GameEntity;

@AbilityAction(AbilityModifierAction.Type.ReviveElemEnergy)
public final class ActionReviveElemEnergy extends AbilityActionHandler {
    @Override
    public boolean execute(
            Ability ability, AbilityModifierAction action, ByteString abilityData, GameEntity target) {
        Grasscutter.getLogger().info("ReviveElemEnergy");
        float ratio = action.ratio.get(ability);

        if (target instanceof EntityAvatar avatar) {
            float curEnergy = avatar.getFightProperty(avatar.getAvatar().getSkillDepot().getElementType().getCurEnergyProp());
            float newEnergy = curEnergy + ratio;

            avatar.getAvatar().setCurrentEnergy(avatar.getAvatar().getSkillDepot().getElementType().getCurEnergyProp(), newEnergy);
            avatar.getScene().broadcastPacket(new PacketEntityFightPropUpdateNotify(avatar, avatar.getAvatar().getSkillDepot().getElementType().getCurEnergyProp()));
            avatar.getScene().broadcastPacket(new PacketEntityFightPropChangeReasonNotify(avatar, avatar.getAvatar().getSkillDepot().getElementType().getCurEnergyProp(), newEnergy, PropChangeReason.PropChangeReason_PROP_CHANGE_ABILITY, ChangeEnergyReason.ChangeEnergyReason_CHANGE_ENERGY_ABILITY));
            Grasscutter.getLogger().info("Revived avatar energy by " + ratio);


            return true;
        }

        return false;
    }
}
