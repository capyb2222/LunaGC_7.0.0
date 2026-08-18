package emu.grasscutter.server.packet.recv;

import emu.grasscutter.data.GameData;
import emu.grasscutter.game.avatar.Avatar;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.*;
import emu.grasscutter.net.proto.AvatarTypeOuterClass;
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

        if (player.getAvatars().getAvatarCount() == 0 && GAME_OPTIONS.newAccountIntro.enabled) {
            // Leaving the account empty is the whole point: the client only runs creation while it
            // has nothing to load. onLogin is deliberately not called, so the session stays in
            // PICKING_CHARACTER, which is the state the router demands before it will accept
            // SetPlayerBornDataReq.
            session.setState(SessionState.PICKING_CHARACTER);

            int notify = GAME_OPTIONS.newAccountIntro.doSetPlayerBornDataNotify;
            if (notify > 0) session.send(new BasePacket(notify));

            emu.grasscutter.Grasscutter.getLogger()
                    .info("[intro] new account, waiting for character creation (notify cmdId={}).",
                            notify > 0 ? notify : "unsent");

            session.send(new PacketPlayerLoginRsp(session));
            return;
        }

        if (player.getAvatars().getAvatarCount() == 0) {
            int avatarId = 10000007;
            Avatar mainCharacter = new Avatar(avatarId);

            if (!GAME_OPTIONS.questing.enabled) {
                mainCharacter.setSkillDepotData(
                    GameData.getAvatarSkillDepotDataMap().get(704));
            }

            player.addAvatar(mainCharacter, false);
            player.setMainCharacterId(avatarId);
            player.setHeadImage(avatarId);
            player
                .getTeamManager()
                .getCurrentSinglePlayerTeamInfo()
                .getAvatars()
                .add(mainCharacter.getAvatarId());
            player.save();

            session.getPlayer().onLogin();
        } else {
            session.getPlayer().onLogin();
        }

        session.send(new PacketPlayerLoginRsp(session));
    }
}
