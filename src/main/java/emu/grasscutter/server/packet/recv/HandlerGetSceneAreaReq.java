package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.*;
import emu.grasscutter.net.proto.GetSceneAreaReqOuterClass.GetSceneAreaReq;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketGetSceneAreaRsp;

@Opcodes(PacketOpcodes.GetSceneAreaReq)
public class HandlerGetSceneAreaReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] header, byte[] payload) throws Exception {
        GetSceneAreaReq req = GetSceneAreaReq.parseFrom(payload);
        int sceneId = req.getSceneId() != 0 ? req.getSceneId() : session.getPlayer().getSceneId();
        session.send(new PacketGetSceneAreaRsp(session.getPlayer(), sceneId));
    }
}
