package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.*;
import emu.grasscutter.net.proto.EvtBeingHitInfoOuterClass.EvtBeingHitInfo;
import emu.grasscutter.net.proto.EvtBeingHitNotifyOuterClass.EvtBeingHitNotify;
import emu.grasscutter.net.proto.ForwardTypeOuterClass.ForwardType;

public class PacketEvtBeingHitNotify extends BasePacket {
    public PacketEvtBeingHitNotify(EvtBeingHitInfo hitInfo) {
        super(PacketOpcodes.EvtBeingHitNotify);

        EvtBeingHitNotify proto = EvtBeingHitNotify.newBuilder()
                .setBeingHitInfo(hitInfo)
                .setForwardType(ForwardType.ForwardType_FORWARD_TO_ALL)
                .build();

        this.setData(proto);
    }
}
