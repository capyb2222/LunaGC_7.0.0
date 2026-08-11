package emu.grasscutter.game.ability.actions;

import com.google.protobuf.ByteString;
import emu.grasscutter.data.binout.AbilityModifier.AbilityModifierAction;
import emu.grasscutter.game.ability.Ability;
import emu.grasscutter.game.entity.GameEntity;

@AbilityAction(AbilityModifierAction.Type.SetAnimatorFloat)
public final class ActionSetAnimatorFloat extends AbilityActionHandler {
    private static final int FLOAT_TYPE = 1;

    @Override
    public boolean execute(
            Ability ability, AbilityModifierAction action, ByteString abilityData, GameEntity target) {
        if (action.animatorParamName == null || target.getScene() == null) return true;
        float val = action.writtenValue().get(ability, 0f);
        ActionSetAnimatorTrigger.broadcastAnimatorParamFloat(target, action.animatorParamName, FLOAT_TYPE, val);
        return true;
    }
}
