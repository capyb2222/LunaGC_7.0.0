package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BasePacket;
import emu.grasscutter.net.packet.PacketOpcodes;
import emu.grasscutter.net.packet.*;
import emu.grasscutter.net.proto.AvatarChangeTraceEffectRspOuterClass;

public class PacketAvatarChangeTraceEffectRsp
extends BasePacket {
    public PacketAvatarChangeTraceEffectRsp(long avatarGuid, int traceEffectId) {
        super(PacketOpcodes.AvatarChangeTraceEffectRsp);
        AvatarChangeTraceEffectRspOuterClass.AvatarChangeTraceEffectRsp proto = AvatarChangeTraceEffectRspOuterClass.AvatarChangeTraceEffectRsp.newBuilder().setAvatarGuid(avatarGuid).setTraceEffectId(traceEffectId).build();
        this.setData(proto);
    }

    public PacketAvatarChangeTraceEffectRsp() {
        super(PacketOpcodes.AvatarChangeTraceEffectRsp);
        AvatarChangeTraceEffectRspOuterClass.AvatarChangeTraceEffectRsp proto = AvatarChangeTraceEffectRspOuterClass.AvatarChangeTraceEffectRsp.newBuilder().setRetcode(1).build();
        this.setData(proto);
    }
}

