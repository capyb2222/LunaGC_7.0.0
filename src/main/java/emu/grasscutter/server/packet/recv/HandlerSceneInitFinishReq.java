package emu.grasscutter.server.packet.recv;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.config.Configuration;
import emu.grasscutter.game.player.Player.SceneLoadState;
import emu.grasscutter.net.packet.*;
import emu.grasscutter.net.proto.SceneInitFinishReqOuterClass.SceneInitFinishReq;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.*;
import emu.grasscutter.utils.WatermarkUtils;

@Opcodes(PacketOpcodes.SceneInitFinishReq)
public class HandlerSceneInitFinishReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] header, byte[] payload) throws Exception {
        SceneInitFinishReq req = SceneInitFinishReq.parseFrom(payload);

        var player = session.getPlayer();
        var world = player.getWorld();

        session.send(new PacketServerTimeNotify());
        session.send(new PacketWorldPlayerInfoNotify(world));
        session.send(new PacketWorldDataNotify(world));
        session.send(new PacketPlayerWorldSceneInfoListNotify(player));
        session.send(new PacketSceneForceUnlockNotify(1, true));
        session.send(new PacketHostPlayerNotify(world));
        session.send(new PacketSceneDataNotify(player.getSceneId()));

        session.send(new PacketSceneTimeNotify(player));
        session.send(new PacketPlayerGameTimeNotify(player));
        session.send(new PacketPlayerEnterSceneInfoNotify(player));
        int moonPhaseCount = (int) player.getTeamManager().getActiveTeam().stream()
                .filter(e -> PacketPlayerEnterSceneInfoNotify.getMoonphaseIds().contains(e.getAvatar().getAvatarId()))
                .count();
        session.send(new PacketTeamMoonPhaseChangeNotify(moonPhaseCount));
        int hexenzirkelCount = (int) player.getTeamManager().getActiveTeam().stream()
                .filter(e -> PacketPlayerEnterSceneInfoNotify.getHexenzirkelIds().contains(e.getAvatar().getAvatarId()))
                .count();
        session.send(new PacketTeamHexenzirkelChangeNotify(hexenzirkelCount));
        session.send(new PacketSceneAreaWeatherNotify(player));
        session.send(new PacketScenePlayerInfoNotify(world));
        session.send(new PacketSceneTeamUpdateNotify(player));

        session.send(new PacketSyncTeamEntityNotify(player));
        session.send(new PacketSyncScenePlayTeamEntityNotify(player));

        session.send(new PacketSceneInitFinishRsp(player));
        session.send(buildWatermarkPacket());

        player.setSceneLoadState(SceneLoadState.INIT);

        player.getScene().playerSceneInitialized(player);
    }

    /**
     * The client resets its watermark on every scene init, so the server-wide text has to be re-sent
     * here rather than once at login. Falls back to the stock UID watermark when disabled, unset, or
     * too long for the payload's single length byte.
     */
    private static BasePacket buildWatermarkPacket() {
        var options = Configuration.GAME_OPTIONS.watermark;

        if (options.enabled && options.text != null && !options.text.isBlank()) {
            if (WatermarkUtils.fits(options.text)) {
                return new PacketWindSeedClientNotify(WatermarkUtils.buildLuac(options.text));
            }
            Grasscutter.getLogger()
                    .warn(
                            "Watermark text is too long ({} bytes, max {}); using the default watermark.",
                            options.text.getBytes(java.nio.charset.StandardCharsets.UTF_8).length,
                            WatermarkUtils.MAX_LENGTH - 1);
        }

        return new PacketWindSeedUID();
    }
}
