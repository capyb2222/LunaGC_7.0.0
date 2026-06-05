package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.*;
import emu.grasscutter.net.proto.GetAreaExplorePointRspOuterClass.GetAreaExplorePointRsp;
import java.util.List;

public class PacketGetAreaExplorePointRsp extends BasePacket {

    public PacketGetAreaExplorePointRsp(List<Integer> areaIds) {
        super(PacketOpcodes.GetAreaExplorePointRsp);

        GetAreaExplorePointRsp.Builder p = GetAreaExplorePointRsp.newBuilder()
            .addAllAreaIdList(areaIds);

        this.setData(p);
    }
}
