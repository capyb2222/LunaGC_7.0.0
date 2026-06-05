package emu.grasscutter.game.ability.actions;

import com.google.protobuf.ByteString;
import emu.grasscutter.Grasscutter;
import emu.grasscutter.data.binout.AbilityModifier.AbilityModifierAction;
import emu.grasscutter.game.ability.Ability;
import emu.grasscutter.game.entity.EntityAvatar;
import emu.grasscutter.game.entity.EntityClientGadget;
import emu.grasscutter.game.entity.GameEntity;

@AbilityAction(AbilityModifierAction.Type.KillSelf)
public final class ActionKillSelf extends AbilityActionHandler {
    @Override
    public boolean execute(
            Ability ability, AbilityModifierAction action, ByteString abilityData, GameEntity target) {
        if (target == null) {
            Grasscutter.getLogger().warn("Tried killing null target");
            return false;
        }

        if (target instanceof EntityAvatar) {
            return true;
        }

        if (target instanceof EntityClientGadget) {
            return true;
        }
        target.getScene().killEntity(target);
        return true;
    }
}
