package emu.grasscutter.server.packet.recv;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.data.GameData;
import emu.grasscutter.game.avatar.Avatar;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.*;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.game.GameSession.SessionState;
import emu.grasscutter.server.packet.send.*;

import static emu.grasscutter.config.Configuration.GAME_OPTIONS;

@Opcodes(PacketOpcodes.PlayerLoginReq)
public class HandlerPlayerLoginReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] header, byte[] payload) throws Exception {
        if (session.getAccount() == null) {
            session.close();
            return;
        }

        Player player = session.getPlayer();
        var intro = GAME_OPTIONS.newAccountIntro;

        if (player.getAvatars().getAvatarCount() == 0 && intro.enabled) {
            session.setState(SessionState.PICKING_CHARACTER);

            if (intro.doSetPlayerBornDataNotify > 0) {
                session.send(new BasePacket(intro.doSetPlayerBornDataNotify));
            }
            Grasscutter.getLogger()
                    .info("[intro] new account, waiting for character creation (notify cmdId={}).",
                            intro.doSetPlayerBornDataNotify > 0 ? intro.doSetPlayerBornDataNotify : "unsent");

            session.send(new PacketPlayerLoginRsp(session));
            this.scheduleFallback(session, player);
            return;
        }

        if (player.getAvatars().getAvatarCount() == 0) {
            createDefaultTraveler(player);
        }

        player.onLogin();
        session.send(new PacketPlayerLoginRsp(session));
    }

    private void scheduleFallback(GameSession session, Player player) {
        int seconds = GAME_OPTIONS.newAccountIntro.fallbackSeconds;
        if (seconds <= 0) return;

        Grasscutter.getGameServer()
                .getScheduler()
                .scheduleDelayedTask(
                        () -> {
                            if (session.getState() != SessionState.PICKING_CHARACTER) return;
                            if (player.getAvatars().getAvatarCount() > 0) return;

                            Grasscutter.getLogger()
                                    .warn("[intro] no character creation after {}s, falling back to the default Traveler.",
                                            seconds);
                            createDefaultTraveler(player);
                            player.onLogin();
                        },
                        seconds);
    }

    private static void createDefaultTraveler(Player player) {
        int avatarId = 10000007;
        Avatar mainCharacter = new Avatar(avatarId);

        if (!GAME_OPTIONS.questing.enabled) {
            mainCharacter.setSkillDepotData(GameData.getAvatarSkillDepotDataMap().get(704));
        }

        player.addAvatar(mainCharacter, false);
        player.setMainCharacterId(avatarId);
        player.setHeadImage(avatarId);
        player.getTeamManager().getCurrentSinglePlayerTeamInfo().getAvatars().add(avatarId);
        player.save();
    }
}
