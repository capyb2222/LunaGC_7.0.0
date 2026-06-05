package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.*;
import emu.grasscutter.net.proto.PingRspOuterClass.PingRsp;

public class PacketPingRsp extends BasePacket {

    public PacketPingRsp(int clientSeq, int time, int seq) {
        super(PacketOpcodes.PingRsp, clientSeq);

        PingRsp p = PingRsp.newBuilder()
                .setClientTime(time)
                .setSeq(seq)
                .build();

        this.setData(p.toByteArray());
    }
}
