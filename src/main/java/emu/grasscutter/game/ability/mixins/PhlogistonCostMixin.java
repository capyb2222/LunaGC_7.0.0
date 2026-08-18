package emu.grasscutter.game.ability.mixins;

import com.google.protobuf.ByteString;
import emu.grasscutter.data.binout.AbilityMixinData;
import emu.grasscutter.game.ability.Ability;
import emu.grasscutter.game.entity.EntityVehicle;
import emu.grasscutter.game.entity.GameEntity;
import emu.grasscutter.game.player.Phlogiston;

@AbilityMixin(value = AbilityMixinData.Type.PhlogistonCostMixin)
public class PhlogistonCostMixin extends AbilityMixinHandler {

    @Override
    public boolean execute(Ability ability, AbilityMixinData mixinData, ByteString abilityData, GameEntity target) {
        float cost = mixinData.speed.get(ability);
        if (cost == 0.0f) cost = Phlogiston.DEFAULT_STEP;

        if (ability.getOwner() instanceof EntityVehicle vehicle) {
            Phlogiston.change(vehicle, -cost);
        } else {
            Phlogiston.change(ability.getPlayerOwner(), -cost);
        }
        return true;
    }
}
