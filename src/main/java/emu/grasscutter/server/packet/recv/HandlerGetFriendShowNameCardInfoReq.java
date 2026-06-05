package emu.grasscutter.server.packet.recv;

import emu.grasscutter.GameConstants;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.*;
import emu.grasscutter.net.proto.GetFriendShowNameCardInfoReqOuterClass;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketGetFriendShowNameCardInfoRsp;
import java.util.List;

@Opcodes(PacketOpcodes.GetFriendShowNameCardInfoReq)
public class HandlerGetFriendShowNameCardInfoReq extends PacketHandler {
    @Override
    public void handle(GameSession session, byte[] header, byte[] payload) throws Exception {
        var req =
                GetFriendShowNameCardInfoReqOuterClass.GetFriendShowNameCardInfoReq.parseFrom(payload);

        int targetUid = req.getUid();
        Player target = session.getServer().getPlayerByUid(targetUid, true);

        if (target == null) {
            if (targetUid == GameConstants.SERVER_CONSOLE_UID) {
                session.send(new PacketGetFriendShowNameCardInfoRsp(targetUid, List.of()));
            }
            return;
        }
        session.send(
                new PacketGetFriendShowNameCardInfoRsp(targetUid, target.getShowNameCardInfoList()));
    }
}
