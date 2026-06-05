package emu.grasscutter.game.ability.actions;

import com.google.protobuf.ByteString;
import emu.grasscutter.data.binout.AbilityModifier.AbilityModifierAction;
import emu.grasscutter.game.ability.Ability;
import emu.grasscutter.game.entity.GameEntity;

@AbilityAction(AbilityModifierAction.Type.SetAnimatorInt)
public final class ActionSetAnimatorInt extends AbilityActionHandler {
    private static final int INT_TYPE = 3;

    @Override
    public boolean execute(
            Ability ability, AbilityModifierAction action, ByteString abilityData, GameEntity target) {
        if (action.animatorParamName == null || target.getScene() == null) return true;
        int val = (int) action.ratio.get(ability, 0f);
        ActionSetAnimatorTrigger.broadcastAnimatorParamInt(target, action.animatorParamName, INT_TYPE, val);
        return true;
    }
}
