package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.*;
import emu.grasscutter.net.proto.AvatarExpeditionStartReqOuterClass.AvatarExpeditionStartReq;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketAvatarExpeditionStartRsp;
import emu.grasscutter.utils.Utils;

@Opcodes(PacketOpcodes.AvatarExpeditionStartReq)
public class HandlerAvatarExpeditionStartReq extends PacketHandler {
    @Override
    public void handle(GameSession session, byte[] header, byte[] payload) throws Exception {
        AvatarExpeditionStartReq req = AvatarExpeditionStartReq.parseFrom(payload);
        var player = session.getPlayer();

        // 7.0 sends a list of expeditions in one request instead of a single flattened one.
        int startTime = Utils.getCurrentSeconds();
        for (var info : req.getBasicInfoListList()) {
            player.addExpeditionInfo(
                    info.getAvatarGuid(), info.getExpId(), info.getHourTime(), startTime);
        }
        player.save();
        session.send(new PacketAvatarExpeditionStartRsp(player.getExpeditionInfo()));
    }
}
