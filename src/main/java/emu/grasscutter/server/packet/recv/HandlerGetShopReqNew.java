package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.*;
import emu.grasscutter.net.proto.GetShopReqNewOuterClass.GetShopReqNew;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketGetShopRspNew;

@Opcodes(PacketOpcodes.GetShopReqNew)
public class HandlerGetShopReqNew extends PacketHandler {
    @Override
    public void handle(GameSession session, byte[] header, byte[] payload) throws Exception {
        GetShopReqNew req = GetShopReqNew.parseFrom(payload);

        session.send(new PacketGetShopRspNew(req.getParam()));
    }
}