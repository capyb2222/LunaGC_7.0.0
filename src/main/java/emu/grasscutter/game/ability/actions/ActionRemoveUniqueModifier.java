package emu.grasscutter.game.ability.actions;
 
import com.google.protobuf.ByteString;
import emu.grasscutter.Grasscutter;
import emu.grasscutter.data.binout.AbilityModifier.AbilityModifierAction;
import emu.grasscutter.game.ability.Ability;
import emu.grasscutter.game.ability.AbilityModifierController;
import emu.grasscutter.game.entity.GameEntity;

@AbilityAction(AbilityModifierAction.Type.RemoveUniqueModifier)
 
public final class ActionRemoveUniqueModifier extends AbilityActionHandler {

    @Override
    public boolean execute(
            Ability ability, AbilityModifierAction action, ByteString abilityData, GameEntity target) {
 
        if (!ability.getModifiers().containsKey(action.modifierName)) {
            Grasscutter.getLogger().debug("Unique Modifier {} not found for removal", action.modifierName);
            return false;
        }

        ability.getModifiers().remove(action.modifierName);
        return true;
    }
}