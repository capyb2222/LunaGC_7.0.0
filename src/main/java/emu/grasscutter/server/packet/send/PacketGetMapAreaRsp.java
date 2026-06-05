package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.*;
import emu.grasscutter.net.proto.GetMapAreaRspOuterClass.GetMapAreaRsp;
import emu.grasscutter.net.proto.MapAreaInfoOuterClass.MapAreaInfo;

public class PacketGetMapAreaRsp extends BasePacket {

    public PacketGetMapAreaRsp() {
        super(PacketOpcodes.GetMapAreaRsp);

        GetMapAreaRsp.Builder p = GetMapAreaRsp.newBuilder();
        for (int i = 1; i <= 200; i++) {
            p.addMapAreaInfoList(
                MapAreaInfo.newBuilder()
                    .setMapAreaId(i)
                    .setIsOpen(true)
                    .build()
            );
        }

        this.setData(p);
    }
}
