package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.*;
import emu.grasscutter.net.proto.TowerTeamOuterClass;
import emu.grasscutter.net.proto.TowerTeamSelectReqOuterClass.TowerTeamSelectReq;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketTowerTeamSelectRsp;

@Opcodes(PacketOpcodes.TowerTeamSelectReq)
public class HandlerTowerTeamSelectReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] header, byte[] payload) throws Exception {
        TowerTeamSelectReq req = TowerTeamSelectReq.parseFrom(payload);

        // 7.0's TowerTeamSelectReq only carries floor_id; the team list has no recoverable name.
        var towerTeams = java.util.List.<java.util.List<Long>>of();

        session.getPlayer().getTowerManager().teamSelect(req.getFloorId(), towerTeams);

        session.send(new PacketTowerTeamSelectRsp());
    }
}
