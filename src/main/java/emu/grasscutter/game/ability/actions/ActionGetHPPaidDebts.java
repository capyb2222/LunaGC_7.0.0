package emu.grasscutter.game.ability.actions;

import com.google.protobuf.ByteString;
import emu.grasscutter.Grasscutter;
import emu.grasscutter.data.binout.AbilityModifier;
import emu.grasscutter.game.ability.Ability;
import emu.grasscutter.game.entity.EntityAvatar;
import emu.grasscutter.game.entity.GameEntity;
import emu.grasscutter.game.props.FightProperty;
import emu.grasscutter.net.proto.ChangeHpDebtsReasonOuterClass;
import emu.grasscutter.net.proto.PropChangeReasonOuterClass;
import emu.grasscutter.server.packet.send.PacketEntityFightPropChangeReasonNotify;
import emu.grasscutter.server.packet.send.PacketEntityFightPropUpdateNotify;

@AbilityAction(value = AbilityModifier.AbilityModifierAction.Type.GetHPPaidDebts)
public final class ActionGetHPPaidDebts extends AbilityActionHandler {
    @Override
    public boolean execute(Ability ability, AbilityModifier.AbilityModifierAction action, ByteString abilityData, GameEntity target) {
        if (target instanceof EntityAvatar) {
            float paiddebt = target.getFightProperty(FightProperty.FIGHT_PROP_CUR_HP_PAID_DEBTS);
            String overrideMapKey = action.overrideMapKey;

            if (paiddebt < 0) {
                paiddebt = 0;
            }

            ability.getAbilitySpecials().put(overrideMapKey, paiddebt);

            target.setFightProperty(FightProperty.FIGHT_PROP_CUR_HP_PAID_DEBTS, paiddebt);

            target.getWorld().broadcastPacket(new PacketEntityFightPropUpdateNotify(target, FightProperty.FIGHT_PROP_CUR_HP_PAID_DEBTS));
            target.getWorld().broadcastPacket(new PacketEntityFightPropChangeReasonNotify(target, FightProperty.FIGHT_PROP_CUR_HP_PAID_DEBTS, paiddebt, PropChangeReasonOuterClass.PropChangeReason.PropChangeReason_PROP_CHANGE_ABILITY, ChangeHpDebtsReasonOuterClass.ChangeHpDebtsReason.CHANGE_HP_DEBTS_REASON_CHANGE_HP_DEBTS_PAY));
        } else {
            Grasscutter.getLogger().warn("[ActionGetHPPaidDebts] CANNOT PAY HPDEBT FOR NON AVATAR ENTITY");
            return false;
        }

        return true;
    }
}
