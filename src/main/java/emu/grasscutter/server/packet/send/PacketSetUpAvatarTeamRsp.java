package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.*;
import emu.grasscutter.net.packet.*;
import emu.grasscutter.net.proto.SetUpAvatarTeamRspOuterClass.SetUpAvatarTeamRsp;

public class PacketSetUpAvatarTeamRsp extends BasePacket {

    public PacketSetUpAvatarTeamRsp(Player player, int teamId, TeamInfo teamInfo) {
        super(PacketOpcodes.SetUpAvatarTeamRsp);

        int maskedTeamId = (teamId - 65504) ^ 27354;
        long maskedCurAvatarGuid = (player.getTeamManager().getCurrentCharacterGuid() + 3667L) ^ 14049L;
        int maskedRetcode = (0 - 51228) ^ 9379;

        SetUpAvatarTeamRsp.Builder proto =
                SetUpAvatarTeamRsp.newBuilder()
                        .setTeamId(maskedTeamId)
                        .setCurAvatarGuid(maskedCurAvatarGuid)
                        .setRetcode(maskedRetcode);

        for (int avatarId : teamInfo.getAvatars()) {

            proto.addAvatarTeamGuidList(player.getAvatars().getAvatarById(avatarId).getGuid());
        }

        this.setData(proto);
    }
}
