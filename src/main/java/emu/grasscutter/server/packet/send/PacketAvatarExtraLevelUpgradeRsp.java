package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.avatar.Avatar;
import emu.grasscutter.net.packet.*;
import emu.grasscutter.net.proto.RetcodeOuterClass.Retcode;
import emu.grasscutter.net.proto._AvatarExtraLevelUpgradeRspOuterClass._AvatarExtraLevelUpgradeRsp;

public class PacketAvatarExtraLevelUpgradeRsp extends BasePacket {

    public PacketAvatarExtraLevelUpgradeRsp(Avatar avatar, int oldLevel) {
        super(PacketOpcodes._AvatarExtraLevelUpgradeRsp);

        this.setData(
                _AvatarExtraLevelUpgradeRsp.newBuilder()
                        .setAvatarGuid(avatar.getGuid())
                        .setOldLevel(oldLevel)
                        .setCurLevel(avatar.getLevel()));
    }

    public PacketAvatarExtraLevelUpgradeRsp(long avatarGuid, Retcode retcode) {
        super(PacketOpcodes._AvatarExtraLevelUpgradeRsp);

        this.setData(
                _AvatarExtraLevelUpgradeRsp.newBuilder()
                        .setAvatarGuid(avatarGuid)
                        .setRetcode(retcode.getNumber()));
    }
}
