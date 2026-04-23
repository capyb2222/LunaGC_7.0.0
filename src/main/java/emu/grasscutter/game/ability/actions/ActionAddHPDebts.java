package emu.grasscutter.game.ability.actions;

import com.google.protobuf.ByteString;
import emu.grasscutter.data.binout.AbilityModifier;
import emu.grasscutter.data.binout.AbilityMixinData;
import emu.grasscutter.game.ability.Ability;
import emu.grasscutter.game.avatar.Avatar;
import emu.grasscutter.game.entity.EntityAvatar;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import emu.grasscutter.game.entity.GameEntity;
import emu.grasscutter.game.entity.EntityWeapon;
import emu.grasscutter.game.props.FightProperty;
import emu.grasscutter.net.proto.ChangeHpDebtsReasonOuterClass;
import emu.grasscutter.net.proto.PropChangeReasonOuterClass;
import emu.grasscutter.server.packet.send.PacketEntityFightPropChangeReasonNotify;
import emu.grasscutter.server.packet.send.PacketEntityFightPropUpdateNotify;
import emu.grasscutter.Grasscutter;

@AbilityAction(value = AbilityModifier.AbilityModifierAction.Type.AddHPDebts)
public final class ActionAddHPDebts extends AbilityActionHandler {
    @Override
    public boolean execute(Ability ability, AbilityModifier.AbilityModifierAction action, ByteString abilityData, GameEntity target) {
        var owner = ability.getOwner();
        if (owner instanceof EntityWeapon weapon) {
            owner = ability.getPlayerOwner().getTeamManager().getCurrentAvatarEntity();
        }
        var properties = new Object2FloatOpenHashMap<String>();

        for (var property : FightProperty.values()) {
            var name = property.name();
            var value = owner.getFightProperty(property);
            properties.put(name, value);
        }

        properties.putAll(ability.getAbilitySpecials());

        float debt = action.ratio.get(properties, 0f);
        Grasscutter.getLogger().info("ActionAddHPDebts executed with debt {}", debt);
        Avatar avatar = ability.getPlayerOwner().getCurrentAvatar();
        float maxValue = action.maxValue.get(ability) * target.getFightProperty(FightProperty.FIGHT_PROP_MAX_HP);

            float curDebt = target.getFightProperty(FightProperty.FIGHT_PROP_CUR_HP_DEBTS);
            float newDebt = curDebt + debt;
            if (newDebt < 0) {
                newDebt = 0;
            }
            if (newDebt > 2 * target.getFightProperty(FightProperty.FIGHT_PROP_MAX_HP)) {
                Grasscutter.getLogger().warn("[ActionAddHPDebts] bond of life surpassed its limit, setting to max");
                newDebt = 2 * target.getFightProperty(FightProperty.FIGHT_PROP_MAX_HP);
            }
            float changeDebt = newDebt - curDebt;
            target.setFightProperty(FightProperty.FIGHT_PROP_CUR_HP_DEBTS, newDebt);
            target.getWorld().broadcastPacket(new PacketEntityFightPropUpdateNotify(target, FightProperty.FIGHT_PROP_CUR_HP_DEBTS));

            if (changeDebt != 0) {
                if (newDebt == 0) {
                    target.getWorld().broadcastPacket(new PacketEntityFightPropChangeReasonNotify(target, FightProperty.FIGHT_PROP_CUR_HP_DEBTS, changeDebt, PropChangeReasonOuterClass.PropChangeReason.PropChangeReason_PROP_CHANGE_ABILITY, ChangeHpDebtsReasonOuterClass.ChangeHpDebtsReason.CHANGE_HP_DEBTS_REASON_CHANGE_HP_DEBTS_PAY_FINISH));
                } else if (changeDebt > 0) {
                    target.getWorld().broadcastPacket(new PacketEntityFightPropChangeReasonNotify(target, FightProperty.FIGHT_PROP_CUR_HP_DEBTS, changeDebt, PropChangeReasonOuterClass.PropChangeReason.PropChangeReason_PROP_CHANGE_ABILITY, ChangeHpDebtsReasonOuterClass.ChangeHpDebtsReason.CHANGE_HP_DEBTS_REASON_CHANGE_HP_DEBTS_ADD_ABILITY));
                } else if (changeDebt < 0) {
                    target.getWorld().broadcastPacket(new PacketEntityFightPropChangeReasonNotify(target, FightProperty.FIGHT_PROP_CUR_HP_DEBTS, changeDebt, PropChangeReasonOuterClass.PropChangeReason.PropChangeReason_PROP_CHANGE_ABILITY, ChangeHpDebtsReasonOuterClass.ChangeHpDebtsReason.CHANGE_HP_DEBTS_REASON_CHANGE_HP_DEBTS_PAY));
                }
            }
        return true;
    }
}
