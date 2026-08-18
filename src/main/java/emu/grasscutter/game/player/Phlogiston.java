package emu.grasscutter.game.player;

import emu.grasscutter.game.entity.EntityVehicle;
import emu.grasscutter.server.packet.send.PacketServerGlobalValueChangeNotify;
import emu.grasscutter.server.packet.send.PacketVehiclePhlogistonPointsNotify;

/** Natlan's exploration fuel, held both by the party and by each Saurian it rides. */
public final class Phlogiston {
    public static final String TEAM_KEY = "SGV_PlayerTeam_Phlogiston";
    public static final float TEAM_MAX = 100f;
    public static final float VEHICLE_MAX = 50f;

    /** What a change with no amount of its own is worth. */
    public static final float DEFAULT_STEP = 5f;

    private Phlogiston() {}

    public static void change(Player player, float delta) {
        if (player == null) return;

        float value = clamp(player.getPhlogistonValue() + delta, TEAM_MAX);
        player.setPhlogistonValue(value);
        player.sendPacket(
                new PacketServerGlobalValueChangeNotify(
                        player.getTeamManager().getEntity().getId(), TEAM_KEY, value));
    }

    /**
     * A Saurian burns its own fuel, including once that fuel is gone - reading an empty tank as
     * "this vehicle has none" is what used to hand the bill to the party instead.
     */
    public static void change(EntityVehicle vehicle, float delta) {
        if (vehicle == null) return;

        float value = clamp(vehicle.getCurPhlogiston() + delta, VEHICLE_MAX);
        vehicle.setCurPhlogiston(value);
        vehicle.getOwner().sendPacket(new PacketVehiclePhlogistonPointsNotify(vehicle));
    }

    private static float clamp(float value, float max) {
        return Math.max(0f, Math.min(max, value));
    }
}
