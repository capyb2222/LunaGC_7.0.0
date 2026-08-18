package emu.grasscutter.game.ability.actions;

import com.google.protobuf.ByteString;
import emu.grasscutter.data.binout.AbilityModifier;
import emu.grasscutter.game.ability.Ability;
import emu.grasscutter.game.entity.EntityVehicle;
import emu.grasscutter.game.entity.GameEntity;
import emu.grasscutter.game.player.Phlogiston;

@AbilityAction(value = AbilityModifier.AbilityModifierAction.Type.ChangePhlogiston)
public final class ActionChangePhlogiston extends AbilityActionHandler {

    @Override
    public boolean execute(Ability ability, AbilityModifier.AbilityModifierAction action, ByteString abilityData, GameEntity target) {
        float amount = action.ratio.get(ability);
        if (amount == 0.0f) amount = Phlogiston.DEFAULT_STEP;
        if (!"Add".equals(action.determineType)) amount = -amount;

        if (ability.getOwner() instanceof EntityVehicle vehicle) {
            Phlogiston.change(vehicle, amount);
        } else {
            Phlogiston.change(ability.getPlayerOwner(), amount);
        }
        return true;
    }
}
