package emu.grasscutter.game.ability.actions;

import com.google.protobuf.ByteString;
import emu.grasscutter.data.binout.AbilityModifier.AbilityModifierAction;
import emu.grasscutter.game.ability.Ability;
import emu.grasscutter.game.entity.GameEntity;

@AbilityAction(AbilityModifierAction.Type.SetAnimatorBool)
public final class ActionSetAnimatorBool extends AbilityActionHandler {
    private static final int BOOL_TYPE = 4;

    @Override
    public boolean execute(
            Ability ability, AbilityModifierAction action, ByteString abilityData, GameEntity target) {
        if (action.animatorParamName == null || target.getScene() == null) return true;
        boolean val = action.writtenValue().get(ability, 0f) != 0f;
        ActionSetAnimatorTrigger.broadcastAnimatorParam(target, action.animatorParamName, BOOL_TYPE, val);
        return true;
    }
}
