package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.avatar.Avatar;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.*;
import emu.grasscutter.net.proto.AvatarDataNotifyOuterClass.AvatarDataNotify;

public class PacketAvatarDataNotify extends BasePacket {

    public PacketAvatarDataNotify(Player player) {
        super(PacketOpcodes.AvatarDataNotify, true);

        AvatarDataNotify.Builder proto =
                AvatarDataNotify.newBuilder()
                        .setCurAvatarTeamId(player.getTeamManager().getCurrentTeamId())
                        .setChooseAvatarGuid(player.getTeamManager().getCurrentCharacterGuid())
                        .addAllOwnedFlycloakList(player.getFlyCloakList())
                        .addAllOwnedCostumeList(player.getCostumeList())
                        .addAllOwnedTraceEffectList(player.getTraceEffectList());

        player.getAvatars().forEach(avatar -> proto.addAvatarList(avatar.toProto()));

        player
                .getTeamManager()
                .getTeams()
                .forEach(
                        (id, teamInfo) -> {
                            proto.putAvatarTeamMap(id, teamInfo.toProto(player));
                            if (id > 4) {

                            }
                        });

        Avatar mainCharacter = player.getAvatars().getAvatarById(player.getMainCharacterId());
        if (mainCharacter != null) {
            proto.setChooseAvatarGuid(mainCharacter.getGuid());
        }

        this.setData(proto.build());
    }
}
