package emu.grasscutter.game.ability.actions;

import com.google.protobuf.ByteString;
import emu.grasscutter.data.binout.AbilityModifier;
import emu.grasscutter.data.binout.AbilityModifier.AbilityModifierAction;
import emu.grasscutter.game.ability.Ability;
import emu.grasscutter.game.ability.AbilityManager;
import emu.grasscutter.game.ability.AbilityTargetSelector;
import emu.grasscutter.game.ability.PredicateEvaluator;
import emu.grasscutter.game.entity.EntityMonster;
import emu.grasscutter.game.entity.GameEntity;
import java.util.List;

@AbilityAction(value = AbilityModifier.AbilityModifierAction.Type.Predicated)
public final class ActionPredicated extends AbilityActionHandler {

    @Override
    public boolean execute(Ability ability, AbilityModifierAction action, ByteString abilityData, GameEntity target) {
        AbilityManager mgr = ability != null ? ability.getManager() : null;
        if (mgr == null) return true;

        // A block with otherTargets asks a question about something else in the scene - the nearest
        // enemy, say - so its predicates and actions run against whatever that picks out, not
        // against the entity carrying the modifier.
        List<GameEntity> selected = AbilityTargetSelector.select(action.otherTargets, ability, target);
        if (selected == null) {
            run(mgr, ability, action, abilityData, target, target);
            return true;
        }

        boolean any = false;
        for (var candidate : selected) {
            if (!PredicateEvaluator.all(action.targetPredicates, ability, ability.getOwner(), candidate, action)) {
                continue;
            }

            any = true;
            dispatch(mgr, ability, action.successActions, abilityData, target, candidate);
        }

        if (any) return true;

        // Scenes whose group scripts are missing hold no monsters on this side, so "is an enemy
        // nearby" is answered no however many the player can see, and anything gated behind it -
        // an avatar's summon, most visibly - would never happen there. Take the block as passing,
        // but only the bookkeeping half of it: actions naming Target are skipped, since there is no
        // target to name and pointing them at the caster would turn a hit into self harm.
        //
        // It runs as the server's own chain, so the marks it writes stay here. They are a guess -
        // this branch fires precisely because the server cannot see what the player is fighting -
        // and a guessed "_HasTarget_Mark = 1" pushed to the client tells it every enemy is a valid
        // one, however far away, which is how out of range attacks started landing.
        if (blindScene(target)) {
            AbilityManager.runServerOwned(
                    () -> dispatchOwnerOnly(mgr, ability, action.successActions, abilityData, target));
            return true;
        }

        dispatch(mgr, ability, action.failActions, abilityData, target, target);
        return true;
    }

    private void run(AbilityManager mgr, Ability ability, AbilityModifierAction action,
                     ByteString abilityData, GameEntity self, GameEntity candidate) {
        boolean pass = PredicateEvaluator.all(action.targetPredicates, ability, ability.getOwner(), candidate, action);
        dispatch(mgr, ability, pass ? action.successActions : action.failActions, abilityData, self, candidate);
    }

    /** True when the scene holds no living monster at all, i.e. the server cannot see enemies here. */
    private boolean blindScene(GameEntity self) {
        var scene = self != null ? self.getScene() : null;
        if (scene == null) return false;

        return scene.getEntities().values().stream()
                .noneMatch(e -> e instanceof EntityMonster && e.isAlive());
    }

    private void dispatchOwnerOnly(AbilityManager mgr, Ability ability, AbilityModifierAction[] actions,
                                   ByteString abilityData, GameEntity self) {
        if (actions == null) return;
        for (var child : actions) {
            if (child == null || "Target".equals(child.target)) continue;
            if (!allowed(child)) continue;
            mgr.executeActionNow(ability, child, abilityData, self);
        }
    }

    /**
     * Children that name Target explicitly act on the picked entity; the rest act on the entity the
     * modifier is attached to, which is what keeps a summon's bookkeeping on its owner rather than
     * on whatever it happened to aim at.
     */
    private void dispatch(AbilityManager mgr, Ability ability, AbilityModifierAction[] actions,
                          ByteString abilityData, GameEntity self, GameEntity candidate) {
        if (actions == null) return;
        for (var child : actions) {
            if (child == null || !allowed(child)) continue;
            // In order, on this thread: a later block reads what an earlier one writes.
            mgr.executeActionNow(ability, child, abilityData, "Target".equals(child.target) ? candidate : self);
        }
    }

    /**
     * A chain the client never asked the server to run stays within state and spawning, however deep
     * it nests. Everything else - damage, healing, killing - belongs to the invocations the client
     * actually sends, where the target is the one it aimed at rather than the one we picked.
     */
    private boolean allowed(AbilityModifierAction action) {
        return !AbilityManager.isServerOwnedChain()
                || AbilityManager.isAllowedInServerOwnedChain(action.type);
    }
}
