package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.*;
import emu.grasscutter.server.game.GameSession;

@Opcodes(PacketOpcodes.Opcode6231)
public class HandlerOpcode6231 extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] header, byte[] payload) throws Exception {
        var scene = session.getPlayer().getScene();
        if (scene == null) return;
        BasePacket pkt = new BasePacket(PacketOpcodes.Opcode6231);
        pkt.setData(payload);
        scene.broadcastPacket(pkt);
    }
}
