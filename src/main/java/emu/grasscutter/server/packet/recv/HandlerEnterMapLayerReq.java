package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.*;
import emu.grasscutter.server.game.GameSession;

@Opcodes(PacketOpcodes.EnterMapLayerReq)
public class HandlerEnterMapLayerReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] header, byte[] payload) throws Exception {
        emu.grasscutter.Grasscutter.getLogger().info("[Gacha] EnterMapLayerReq received — sending EnterMapLayerRsp (retcode=0)");
        session.send(new BasePacket(PacketOpcodes.EnterMapLayerRsp));
    }
}
