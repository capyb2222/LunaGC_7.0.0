package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.avatar.Avatar;
import emu.grasscutter.net.packet.*;
import emu.grasscutter.net.proto._AvatarExtraLevelNotifyOuterClass._AvatarExtraLevelNotify;

public class PacketAvatarExtraLevelNotify extends BasePacket {

    public PacketAvatarExtraLevelNotify(Avatar avatar) {
        super(PacketOpcodes._AvatarExtraLevelNotify);

        this.setData(
                _AvatarExtraLevelNotify.newBuilder()
                        .setAvatarGuid(avatar.getGuid())
                        .setExtraLevel(avatar.getExtraLevel())
                        .setEntityId(avatar.getEntityId()));
    }
}
