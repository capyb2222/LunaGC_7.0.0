package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.*;
import emu.grasscutter.net.proto.PacketHeadOuterClass.PacketHead;
import emu.grasscutter.net.proto.PingReqOuterClass.PingReq;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketPingRsp;

@Opcodes(PacketOpcodes.PingReq)
public class HandlerPingReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] header, byte[] payload) throws Exception {
        PacketHead head = PacketHead.parseFrom(header);
        var req = PingReq.parseFrom(payload);
        var clientTime = req.getClientTime();
        var seq = req.getSeq();

        session.updateLastPingTime(clientTime);

        session.send(new PacketPingRsp(head.getClientSequenceId(), clientTime, seq));
    }
}
