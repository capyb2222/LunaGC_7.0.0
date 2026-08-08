package emu.grasscutter.server.packet.recv;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.config.Configuration;
import emu.grasscutter.game.player.Player.SceneLoadState;
import emu.grasscutter.net.packet.*;
import emu.grasscutter.net.proto.SceneInitFinishReqOuterClass.SceneInitFinishReq;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.*;
import emu.grasscutter.utils.RichTextUtils;
import emu.grasscutter.utils.WatermarkUtils;
import java.nio.charset.StandardCharsets;

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
            var payload = applyColor(options);

            if (WatermarkUtils.fits(payload)) {
                return new PacketWindSeedClientNotify(WatermarkUtils.buildLuac(payload));
            }

            // Colour markup can push a perfectly good text over the cap, so retry bare before
            // giving up entirely - the right text in the wrong colour beats the wrong text.
            Grasscutter.getLogger()
                    .warn(
                            "Watermark is too long once coloured ({} bytes, max {}); falling back to plain text.",
                            payload.getBytes(StandardCharsets.UTF_8).length,
                            WatermarkUtils.MAX_LENGTH - 1);

            if (WatermarkUtils.fits(options.text)) {
                return new PacketWindSeedClientNotify(WatermarkUtils.buildLuac(options.text));
            }

            Grasscutter.getLogger()
                    .warn("Watermark text alone is still too long; using the default watermark.");
        }

        return new PacketWindSeedUID();
    }

    /** Wraps the configured text in colour markup, or returns it unchanged if none is configured. */
    private static String applyColor(Configuration.GameOptions.WatermarkOptions options) {
        int from = RichTextUtils.parseColor(options.color);
        if (from < 0) {
            if (options.color != null && !options.color.isBlank()) {
                Grasscutter.getLogger()
                        .warn("Watermark colour '{}' is not valid hex; leaving the client default.", options.color);
            }
            return options.text;
        }

        int to = RichTextUtils.parseColor(options.gradientTo);
        return to < 0
                ? RichTextUtils.colorize(options.text, from)
                : RichTextUtils.gradient(options.text, from, to);
    }
}
