package emu.grasscutter.game.ability.actions;

import com.google.protobuf.ByteString;
import emu.grasscutter.Grasscutter;
import emu.grasscutter.data.binout.AbilityModifier.AbilityModifierAction;
import emu.grasscutter.game.ability.Ability;
import emu.grasscutter.game.entity.GameEntity;

@AbilityAction(AbilityModifierAction.Type.RemoveModifier)
public final class ActionRemoveModifier extends AbilityActionHandler {
    @Override
    public boolean execute(
            Ability ability, AbilityModifierAction action, ByteString abilityData, GameEntity target) {
        Grasscutter.getLogger().debug("[Ability] Removing Modifier: {}", action.modifierName);

        if (!ability.getModifiers().containsKey(action.modifierName)) {
            Grasscutter.getLogger().debug("Modifier {} not found for removal", action.modifierName);
            return false;
        }

        ability.getModifiers().remove(action.modifierName);
        return true;
    }
}
