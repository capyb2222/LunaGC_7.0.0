package emu.grasscutter.game.ability.actions;

import com.google.protobuf.ByteString;
import emu.grasscutter.data.binout.AbilityModifier.AbilityModifierAction;
import emu.grasscutter.game.ability.Ability;
import emu.grasscutter.game.entity.GameEntity;

@AbilityAction(AbilityModifierAction.Type.ResetAnimatorTrigger)
public final class ActionResetAnimatorTrigger extends AbilityActionHandler {
    private static final int TRIGGER_TYPE = 9;

    @Override
    public boolean execute(
            Ability ability, AbilityModifierAction action, ByteString abilityData, GameEntity target) {
        if (action.animatorParamName == null || target.getScene() == null) return true;
        ActionSetAnimatorTrigger.broadcastAnimatorParam(target, action.animatorParamName, TRIGGER_TYPE, false);
        return true;
    }
}
