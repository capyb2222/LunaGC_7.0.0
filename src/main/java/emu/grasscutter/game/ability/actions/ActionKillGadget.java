package emu.grasscutter.game.ability.actions;

import com.google.protobuf.ByteString;
import emu.grasscutter.data.binout.AbilityModifier.AbilityModifierAction;
import emu.grasscutter.game.ability.Ability;
import emu.grasscutter.game.entity.EntityGadget;
import emu.grasscutter.game.entity.GameEntity;

/**
 * Removes a gadget an ability asked to be rid of.
 *
 * <p>Deliberately narrow: only gadgets the caster itself summoned. The action names its target by
 * config id alone, and the same id can stand in the scene for reasons that have nothing to do with
 * this ability - scene furniture, another player's summon - so anything without a matching owner is
 * left where it is. That keeps a summon disappearing on cue from ever meaning the scene loses
 * something it needed.
 */
@AbilityAction(AbilityModifierAction.Type.KillGadget)
public final class ActionKillGadget extends AbilityActionHandler {

    @Override
    public boolean execute(
            Ability ability, AbilityModifierAction action, ByteString abilityData, GameEntity target) {
        var configId = action.gadgetInfo != null ? action.gadgetInfo.configID : 0;
        if (configId == 0 || target == null) return false;

        var scene = target.getScene();
        var owner = ability != null ? ability.getOwner() : null;
        if (scene == null || owner == null) return false;

        // Collected before removing: killEntity edits the same map this walks.
        var doomed =
                scene.getEntities().values().stream()
                        .filter(
                                entity ->
                                        entity instanceof EntityGadget gadget
                                                && gadget.getGadgetId() == configId
                                                && gadget.getOwner() == owner)
                        .toList();

        doomed.forEach(scene::killEntity);
        return true;
    }
}
