package emu.grasscutter.game.ability.mixins;

import com.google.protobuf.ByteString;
import emu.grasscutter.data.binout.AbilityMixinData;
import emu.grasscutter.game.ability.Ability;
import emu.grasscutter.game.entity.EntityVehicle;
import emu.grasscutter.server.packet.send.PacketVehiclePhlogistonPointsNotify;
import emu.grasscutter.game.entity.GameEntity;
import emu.grasscutter.game.managers.stamina.Consumption;
import emu.grasscutter.game.managers.stamina.ConsumptionType;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.game.managers.stamina.StaminaManager;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.server.packet.send.PacketServerGlobalValueChangeNotify;
import emu.grasscutter.Grasscutter;

@AbilityMixin(value = AbilityMixinData.Type.PhlogistonCostMixin)
public class PhlogistonCostMixin extends AbilityMixinHandler {

    @Override
    public boolean execute(Ability ability, AbilityMixinData mixinData, ByteString abilityData, GameEntity target) {
        Player player = ability.getPlayerOwner();
        var owner = ability.getOwner();
        float curPhlogiston = player.getPhlogistonValue();
        float consume = mixinData.speed.get(ability);
        if (consume == 0.0f) consume = 5.0f; // i have no current solution for this so sob, only for some globalspecials
        float updatedPhlogistonValue = curPhlogiston - consume;
        updatedPhlogistonValue = Math.max(0, Math.min(100, updatedPhlogistonValue));
        if (owner instanceof EntityVehicle vehicle) {
            float curVehiclePhlogiston = vehicle.getCurPhlogiston();
            if (curVehiclePhlogiston != 0.0f) {
                
            Grasscutter.getLogger().debug("Current Vehicle Phlogiston Value: {}", curVehiclePhlogiston);
            updatedPhlogistonValue = curVehiclePhlogiston - consume;
            updatedPhlogistonValue = Math.max(0, Math.min(50, updatedPhlogistonValue));
            vehicle.setCurPhlogiston(updatedPhlogistonValue);
            ability.getPlayerOwner().sendPacket(new PacketVehiclePhlogistonPointsNotify(vehicle));
            Grasscutter.getLogger().debug("Updated Vehicle Phlogiston Value: {}", updatedPhlogistonValue);
            return true;
            }
            if (curVehiclePhlogiston == 0.0f) { 
                updatedPhlogistonValue = curPhlogiston - consume;
                updatedPhlogistonValue = Math.max(0, Math.min(100, updatedPhlogistonValue));
                ability.getPlayerOwner().setPhlogistonValue(updatedPhlogistonValue);
                ability.getPlayerOwner().sendPacket(new PacketServerGlobalValueChangeNotify(
ability.getPlayerOwner().getTeamManager().getEntity().getId(),
    "SGV_PlayerTeam_Phlogiston", 
    updatedPhlogistonValue 
    ));
    return true;
            }
        }
        player.setPhlogistonValue(updatedPhlogistonValue);
        player.sendPacket(new PacketServerGlobalValueChangeNotify(
            ability.getPlayerOwner().getTeamManager().getEntity().getId(),
                "SGV_PlayerTeam_Phlogiston", 
                updatedPhlogistonValue 
                ));
        return true;
    }
}