package emu.grasscutter.game.ability.actions;

import java.util.HashMap;

import com.google.protobuf.ByteString;
import emu.grasscutter.data.binout.AbilityModifier;
import emu.grasscutter.game.ability.Ability;
import emu.grasscutter.game.ability.AbilityManager;
import emu.grasscutter.game.ability.actions.AbilityActionHandler;
import emu.grasscutter.data.common.DynamicFloat;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import emu.grasscutter.game.props.FightProperty;
import emu.grasscutter.game.props.PlayerProperty;
import emu.grasscutter.game.entity.GameEntity;
import emu.grasscutter.game.avatar.*;
import emu.grasscutter.game.entity.EntityVehicle;
import emu.grasscutter.net.proto.AbilityScalarTypeOuterClass;
import emu.grasscutter.game.ability.mixins.*;
import emu.grasscutter.net.proto.AbilityScalarValueEntryOuterClass;
import emu.grasscutter.net.proto.AbilityStringOuterClass;
import emu.grasscutter.net.proto.AbilitySyncStateInfoOuterClass.AbilitySyncStateInfo;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.player.*;
import emu.grasscutter.server.packet.send.PacketPlayerPropChangeNotify;
import emu.grasscutter.server.packet.send.PacketPlayerPropChangeReasonNotify;
import emu.grasscutter.server.packet.send.PacketVehiclePhlogistonPointsNotify;
import emu.grasscutter.server.packet.send.PacketServerGlobalValueChangeNotify;
import emu.grasscutter.net.proto.PropChangeReasonOuterClass.PropChangeReason;
import emu.grasscutter.game.entity.EntityAvatar;
import emu.grasscutter.utils.*;
import emu.grasscutter.Grasscutter;

@AbilityAction(value = AbilityModifier.AbilityModifierAction.Type.ChangePhlogiston)
public final class ActionChangePhlogiston extends AbilityActionHandler {

    @Override
    public boolean execute(Ability ability, AbilityModifier.AbilityModifierAction action, ByteString abilityData, GameEntity target) {
        var owner = ability.getOwner();
        Player player = ability.getPlayerOwner();
        float curPhlogiston = player.getPhlogistonValue();
        float consume = action.ratio.get(ability);
        if (consume == 0.0f) consume = 5.0f;
        String determineType = action.determineType;
        float updatedPhlogistonValue = curPhlogiston - consume;
        if (determineType != null) {
            if ("Lose".equals(determineType)) {
                updatedPhlogistonValue = curPhlogiston - consume;
            } else if ("Add".equals(determineType)) {
                updatedPhlogistonValue = curPhlogiston + consume;
            }
        }
        updatedPhlogistonValue = Math.max(0, Math.min(100, updatedPhlogistonValue)); 
        if (owner instanceof EntityVehicle vehicle) {
            float curVehiclePhlogiston = vehicle.getCurPhlogiston();
            if (curVehiclePhlogiston != 0.0f) {

            updatedPhlogistonValue = curVehiclePhlogiston - consume;
            updatedPhlogistonValue = Math.max(0, Math.min(50, updatedPhlogistonValue));
            vehicle.setCurPhlogiston(updatedPhlogistonValue);
            ability.getPlayerOwner().sendPacket(new PacketVehiclePhlogistonPointsNotify(vehicle));
            return true;
            }

            if (curVehiclePhlogiston == 0.0f) { 
                updatedPhlogistonValue = curPhlogiston - consume;
                updatedPhlogistonValue = Math.max(0, Math.min(100, updatedPhlogistonValue));
                ability.getPlayerOwner().setPhlogistonValue(updatedPhlogistonValue);
                ability.getPlayerOwner().sendPacket(new PacketServerGlobalValueChangeNotify(
                    ability.getPlayerOwner().getTeamManager().getEntity().getId(),
                        "SGV_PlayerTeam_Phlogiston", updatedPhlogistonValue 
                    ));
                return true;
            }
        }
            ability.getPlayerOwner().setPhlogistonValue(updatedPhlogistonValue);
            ability.getPlayerOwner().sendPacket(new PacketServerGlobalValueChangeNotify(
                ability.getPlayerOwner().getTeamManager().getEntity().getId(),
                "SGV_PlayerTeam_Phlogiston", updatedPhlogistonValue 
            ));
        return true;
    }
}