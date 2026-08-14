package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.*;
import emu.grasscutter.net.proto.PacketHeadOuterClass.PacketHead;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.utils.ProtoRead;
import emu.grasscutter.server.packet.send.PacketPingRsp;

@Opcodes(PacketOpcodes.PingReq)
public class HandlerPingReq extends PacketHandler {

    // 7.0 renumbered this one too. Unlike the token request it does not throw when read through the
    // generated class - 7.0's numbers simply are not declared there, so protobuf skips them all as
    // unknown and every getter returns 0. That is worse than a throw: the session's last-ping time
    // would sit at 0 and the connection would look idle while the client was pinging it. #1 is the
    // client's clock (a plausible unix time to the second); #7 was 60 on the one captured ping,
    // where a sequence number is the only thing that shape usually is - but one ping cannot show a
    // counter incrementing, so treat seq as unconfirmed until a second capture disagrees.
    private static final int F_CLIENT_TIME = 1; // 6.7: 12
    private static final int F_SEQ = 7; // 6.7: 6, unconfirmed

    @Override
    public void handle(GameSession session, byte[] header, byte[] payload) throws Exception {
        PacketHead head = PacketHead.parseFrom(header);
        var clientTime = (int) ProtoRead.varint(payload, F_CLIENT_TIME);
        var seq = (int) ProtoRead.varint(payload, F_SEQ);

        session.updateLastPingTime(clientTime);

        session.send(new PacketPingRsp(head.getClientSequenceId(), clientTime, seq));
    }
}
