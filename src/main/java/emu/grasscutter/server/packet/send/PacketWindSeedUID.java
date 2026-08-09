package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BasePacket;
import emu.grasscutter.utils.FileUtils;

public class PacketWindSeedUID extends BasePacket {
    public PacketWindSeedUID() {
        super(PacketWindSeedClientNotify.cmdId());
        this.setData(PacketWindSeedClientNotify.encode(FileUtils.readResource("/lua/UID.luac")));
    }
}
