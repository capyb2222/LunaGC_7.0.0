package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.*;
import emu.grasscutter.net.proto.TeamMoonPhaseChangeNotifyOuterClass.TeamMoonPhaseChangeNotify;

public final class PacketTeamMoonPhaseChangeNotify extends BasePacket {

    /** MOON_PHASE_CONST_VALUE_MOON_PHASE_TEAM_ID: the party's own Moonsign, not an event's. */
    private static final int MOON_PHASE_TYPE = 10000;

    public PacketTeamMoonPhaseChangeNotify(int moonsignLevel) {
        super(PacketOpcodes.TeamMoonPhaseChangeNotify);

        this.setData(
                TeamMoonPhaseChangeNotify.newBuilder()
                        .setMoonPhaseType(MOON_PHASE_TYPE)
                        .setMoonPhaseLevel(moonsignLevel));
    }
}
