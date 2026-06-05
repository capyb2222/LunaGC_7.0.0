package emu.grasscutter.server.packet.recv;

import emu.grasscutter.data.GameData;
import emu.grasscutter.data.binout.ScenePointEntry;
import emu.grasscutter.net.packet.*;
import emu.grasscutter.net.proto.SceneTransToPointReqOuterClass.SceneTransToPointReq;
import emu.grasscutter.server.event.player.PlayerTeleportEvent.TeleportType;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketSceneTransToPointRsp;

@Opcodes(PacketOpcodes.SceneTransToPointReq)
public class HandlerSceneTransToPointReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] header, byte[] payload) throws Exception {
        SceneTransToPointReq req = SceneTransToPointReq.parseFrom(payload);
        var player = session.getPlayer();

        ScenePointEntry scenePointEntry =
                GameData.getScenePointEntryById(req.getSceneId(), req.getPointId());

        if (scenePointEntry == null) {
            emu.grasscutter.Grasscutter.getLogger().warn(
                "[SceneTransToPoint] FAIL: no scene point for sceneId={} pointId={} (entry not loaded)",
                req.getSceneId(), req.getPointId());
            session.send(new PacketSceneTransToPointRsp());
            return;
        }

        if (player.getCurHomeWorld().isInHome(player)) {
            session
                    .getServer()
                    .getHomeWorldMPSystem()
                    .leaveCoop(
                            player, req.getSceneId(), scenePointEntry.getPointData().getTranPos().clone());
            session.send(new PacketSceneTransToPointRsp(player, req.getPointId(), req.getSceneId()));
            return;
        }

        boolean ok = player.getWorld().transferPlayerToScene(
                player,
                req.getSceneId(),
                TeleportType.WAYPOINT,
                scenePointEntry.getPointData().getTranPos().clone());
        if (ok) {
            session.send(new PacketSceneTransToPointRsp(player, req.getPointId(), req.getSceneId()));
            return;
        }

        emu.grasscutter.Grasscutter.getLogger().warn(
            "[SceneTransToPoint] FAIL: transferPlayerToScene returned false for sceneId={} pointId={}",
            req.getSceneId(), req.getPointId());
        emu.grasscutter.Grasscutter.getLogger().warn(
            "[SceneTransToPoint] FAIL sceneId={} pointId={} entry={}",
            req.getSceneId(), req.getPointId(),
            scenePointEntry == null ? "null" : "found-but-transfer-failed");
        session.send(new PacketSceneTransToPointRsp());
    }
}
