package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.*;
import emu.grasscutter.server.game.GameSession;

@Opcodes(PacketOpcodes.UnlockMapLayerGroupReq)
public class HandlerUnlockMapLayerGroupReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] header, byte[] payload) throws Exception {
        emu.grasscutter.Grasscutter.getLogger().info("[Gacha] UnlockMapLayerGroupReq received — sending UnlockMapLayerGroupRsp (retcode=0)");
        session.send(new BasePacket(PacketOpcodes.UnlockMapLayerGroupRsp));
    }
}
