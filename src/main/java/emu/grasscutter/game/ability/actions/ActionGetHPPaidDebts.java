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
        Grasscutter.getLogger().warn("gethppaiddebts" + abilityData);

        if (target instanceof EntityAvatar) {
            float paiddebt = target.getFightProperty(FightProperty.FIGHT_PROP_CUR_HP_PAID_DEBTS);
            String overrideMapKey = action.overrideMapKey;

            // Ensure paid debt is non-negative
            if (paiddebt < 0) {
                paiddebt = 0;
            }

            // Override the value in the ability specials map using the overrideMapKey
            // Concatenate overrideMapKey with paiddebt directly
            String newKey = overrideMapKey + paiddebt;

            // Store the value in the map using the new key
            ability.getAbilitySpecials().put(newKey, paiddebt);
            // Store the paid debt value in the override map

            // Optionally update the entity's fight property
            target.setFightProperty(FightProperty.FIGHT_PROP_CUR_HP_PAID_DEBTS, paiddebt);

            // Broadcast the updated paid debt value to the world
            target.getWorld().broadcastPacket(new PacketEntityFightPropUpdateNotify(target, FightProperty.FIGHT_PROP_CUR_HP_PAID_DEBTS));
            target.getWorld().broadcastPacket(new PacketEntityFightPropChangeReasonNotify(target, FightProperty.FIGHT_PROP_CUR_HP_PAID_DEBTS, paiddebt, PropChangeReasonOuterClass.PropChangeReason.PropChangeReason_PROP_CHANGE_ABILITY, ChangeHpDebtsReasonOuterClass.ChangeHpDebtsReason.CHANGE_HP_DEBTS_REASON_CHANGE_HP_DEBTS_PAY));
        } else {
            Grasscutter.getLogger().warn("[ActionGetHPPaidDebts] CANNOT PAY HPDEBT FOR NON AVATAR ENTITY");
            return false;
        }

        return true;
    }
}
