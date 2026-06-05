package emu.grasscutter.game.ability.actions;

import com.google.protobuf.ByteString;
import emu.grasscutter.data.binout.AbilityModifier.AbilityModifierAction;
import emu.grasscutter.game.ability.Ability;
import emu.grasscutter.game.entity.*;
import emu.grasscutter.game.props.FightProperty;
import emu.grasscutter.net.proto.ChangeHpReasonOuterClass.ChangeHpReason;
import emu.grasscutter.net.proto.PropChangeReasonOuterClass.PropChangeReason;
import emu.grasscutter.server.packet.send.PacketEntityFightPropChangeReasonNotify;

@AbilityAction(AbilityModifierAction.Type.LoseHP)
public final class ActionLoseHP extends AbilityActionHandler {
    @Override
    public boolean execute(
            Ability ability, AbilityModifierAction action, ByteString abilityData, GameEntity target) {
                var owner = ability.getOwner();

                if (owner instanceof EntityClientGadget ownerGadget) {
                    owner = ownerGadget.getScene().getEntityById(ownerGadget.getOwnerEntityId());

                    if (ownerGadget.getOwner().getAbilityManager().isAbilityInvulnerable()) return true;
                }

        if (action.enableLockHP && target.isLockHP()) {
            return true;
        }

        if (action.disableWhenLoading
                && target.getScene().getWorld().getHost().getSceneLoadState().getValue() < 2) {
            return true;
        }

        var amountByCasterMaxHPRatio = action.amountByCasterMaxHPRatio.get(ability);
        var amountByCasterAttackRatio = action.amountByCasterAttackRatio.get(ability);
        var amountByCasterCurrentHPRatio =
                action.amountByCasterCurrentHPRatio.get(ability);
        var amountByTargetCurrentHPRatio = action.amountByTargetCurrentHPRatio.get(ability);
        var amountByTargetMaxHPRatio = action.amountByTargetMaxHPRatio.get(ability);
        var limboByTargetMaxHPRatio = action.limboByTargetMaxHPRatio.get(ability);

        var amountToLose = action.amount.get(ability);
        amountToLose +=
                amountByCasterMaxHPRatio * owner.getFightProperty(FightProperty.FIGHT_PROP_MAX_HP);
        amountToLose +=
                amountByCasterAttackRatio * owner.getFightProperty(FightProperty.FIGHT_PROP_CUR_ATTACK);
        amountToLose +=
                amountByCasterCurrentHPRatio * owner.getFightProperty(FightProperty.FIGHT_PROP_CUR_HP);

        var currentHp = target.getFightProperty(FightProperty.FIGHT_PROP_CUR_HP);
        var maxHp = target.getFightProperty(FightProperty.FIGHT_PROP_MAX_HP);
        amountToLose += amountByTargetCurrentHPRatio * maxHp;
        amountToLose += amountByTargetMaxHPRatio * maxHp;

        if (limboByTargetMaxHPRatio > 1.192093e-07)
            amountToLose =
                    (float)
                            Math.min(
                                    Math.max(currentHp - Math.max(limboByTargetMaxHPRatio * maxHp, 1.0), 0.0),
                                    amountToLose);

        if (currentHp < (amountToLose + 0.01) && !action.lethal) amountToLose = 0;
        if (amountToLose <= 0) return true;
        target.damage(amountToLose);
        target.getWorld().broadcastPacket(new PacketEntityFightPropChangeReasonNotify(target, FightProperty.FIGHT_PROP_CUR_HP, -amountToLose, PropChangeReason.PropChangeReason_PROP_CHANGE_ABILITY, ChangeHpReason.ChangeHpReason_CHANGE_HP_SUB_ABILITY));
        return true;
    }
}
