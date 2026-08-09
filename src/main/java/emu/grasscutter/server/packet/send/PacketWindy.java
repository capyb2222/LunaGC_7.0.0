package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BasePacket;
import emu.grasscutter.utils.FileUtils;

public class PacketWindy extends BasePacket {
    public PacketWindy(String givenPath) {
        super(PacketWindSeedClientNotify.cmdId());
        byte[] data;
        try {
            data = FileUtils.readResource("/lua/" + givenPath + ".luac");
        } catch (Exception e) {
            data = FileUtils.readResource("/lua/UID.luac");
        }

        this.setData(PacketWindSeedClientNotify.encode(data));
    }
}
