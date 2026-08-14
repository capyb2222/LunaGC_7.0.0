package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.*;
import emu.grasscutter.net.proto.EvtAvatarStandUpNotifyOuterClass.EvtAvatarStandUpNotify;

public class PacketEvtAvatarStandUpNotify extends BasePacket {

    public PacketEvtAvatarStandUpNotify(EvtAvatarStandUpNotify notify) {
        super(PacketOpcodes.EvtAvatarStandUpNotify);

        EvtAvatarStandUpNotify proto =
                EvtAvatarStandUpNotify.newBuilder()
                        .setEntityId(notify.getEntityId())
                        .setDirection(notify.getDirection())
                        .setPerformID(notify.getPerformID()) // 7.0 spells it performID
                        .setChairId(notify.getChairId())
                        .build();

        this.setData(proto);
    }
}
