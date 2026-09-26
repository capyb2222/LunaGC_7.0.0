package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.*;
import emu.grasscutter.net.proto._AvatarExtraLevelUpgradeReqOuterClass._AvatarExtraLevelUpgradeReq;
import emu.grasscutter.server.game.GameSession;

@Opcodes(PacketOpcodes._AvatarExtraLevelUpgradeReq)
public class HandlerAvatarExtraLevelUpgradeReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] header, byte[] payload) throws Exception {
        var req = _AvatarExtraLevelUpgradeReq.parseFrom(payload);

        session
                .getServer()
                .getInventorySystem()
                .upgradeAvatarExtraLevel(session.getPlayer(), req.getAvatarGuid());
    }
}
