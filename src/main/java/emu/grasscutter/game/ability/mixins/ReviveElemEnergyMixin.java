package emu.grasscutter.game.ability.mixins;

import com.google.protobuf.ByteString;
import emu.grasscutter.data.binout.AbilityMixinData;
import emu.grasscutter.game.ability.Ability;
import emu.grasscutter.game.entity.GameEntity;
import emu.grasscutter.game.managers.stamina.Consumption;
import emu.grasscutter.game.managers.stamina.ConsumptionType;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.game.managers.stamina.StaminaManager;
import emu.grasscutter.game.entity.EntityAvatar;
import emu.grasscutter.net.proto.PropChangeReasonOuterClass.PropChangeReason;
import emu.grasscutter.server.packet.send.PacketEntityFightPropUpdateNotify;
import emu.grasscutter.server.packet.send.PacketEntityFightPropChangeReasonNotify;
import emu.grasscutter.net.proto.ChangeEnergyReasonOuterClass.ChangeEnergyReason;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.server.packet.send.PacketServerGlobalValueChangeNotify;
import emu.grasscutter.Grasscutter;

@AbilityMixin(value = AbilityMixinData.Type.ReviveElemEnergyMixin)
public class ReviveElemEnergyMixin extends AbilityMixinHandler {

    @Override
    public boolean execute(Ability ability, AbilityMixinData mixinData, ByteString abilityData, GameEntity target) {

        float ratio = mixinData.ratio.get(ability);

        if (target instanceof EntityAvatar avatar) {
            float curEnergy = avatar.getFightProperty(avatar.getAvatar().getSkillDepot().getElementType().getCurEnergyProp());
            float newEnergy = curEnergy + ratio;

            avatar.getAvatar().setCurrentEnergy(avatar.getAvatar().getSkillDepot().getElementType().getCurEnergyProp(), newEnergy);
            avatar.getScene().broadcastPacket(new PacketEntityFightPropUpdateNotify(avatar, avatar.getAvatar().getSkillDepot().getElementType().getCurEnergyProp()));
            avatar.getScene().broadcastPacket(new PacketEntityFightPropChangeReasonNotify(avatar, avatar.getAvatar().getSkillDepot().getElementType().getCurEnergyProp(), newEnergy, PropChangeReason.PropChangeReason_PROP_CHANGE_ABILITY, ChangeEnergyReason.ChangeEnergyReason_CHANGE_ENERGY_ABILITY));
            Grasscutter.getLogger().info("Revived avatar energy (mixin) by " + ratio);

            return true;

        }

        return false;

    }
}
