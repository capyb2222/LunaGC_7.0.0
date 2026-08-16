package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.*;
import emu.grasscutter.server.game.GameSession;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Opcodes(PacketOpcodes.PlayerForceExitReq)
public class HandlerPlayerForceExitReq extends PacketHandler {
    @Override
    public void handle(GameSession session, byte[] header, byte[] payload) throws Exception {
        // Client should auto disconnect right now
        session.send(new BasePacket(PacketOpcodes.PlayerForceExitRsp));
        // Was a whole thread spawned per logout just to wait a second before closing
        CompletableFuture.runAsync(
                session::close, CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS));
    }
}
