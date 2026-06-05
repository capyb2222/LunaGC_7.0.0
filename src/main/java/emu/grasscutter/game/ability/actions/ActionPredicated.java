package emu.grasscutter.game.ability.actions;

import com.google.protobuf.ByteString;
import emu.grasscutter.data.binout.AbilityModifier;
import emu.grasscutter.data.binout.AbilityModifier.AbilityModifierAction;
import emu.grasscutter.game.ability.Ability;
import emu.grasscutter.game.ability.AbilityManager;
import emu.grasscutter.game.ability.PredicateEvaluator;
import emu.grasscutter.game.entity.GameEntity;

@AbilityAction(value = AbilityModifier.AbilityModifierAction.Type.Predicated)
public final class ActionPredicated extends AbilityActionHandler {

    @Override
    public boolean execute(Ability ability, AbilityModifierAction action, ByteString abilityData, GameEntity target) {
        boolean pass = PredicateEvaluator.all(action.targetPredicates, ability, ability.getOwner(), target, action);
        AbilityModifierAction[] toRun = pass ? action.successActions : action.failActions;
        if (toRun == null) return true;
        AbilityManager mgr = ability.getManager();
        if (mgr == null) return true;
        for (var child : toRun) {
            if (child == null) continue;
            mgr.executeAction(ability, child, abilityData, target);
        }
        return true;
    }
}
