package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.*;
import emu.grasscutter.net.proto.AiSyncInfoOuterClass.AiSyncInfo;
import emu.grasscutter.net.proto.EntityAiSyncNotifyOuterClass.EntityAiSyncNotify;

public class PacketEntityAiSyncNotify extends BasePacket {

    public PacketEntityAiSyncNotify(EntityAiSyncNotify notify) {
        super(PacketOpcodes.EntityAiSyncNotify, true);

        EntityAiSyncNotify.Builder proto = EntityAiSyncNotify.newBuilder();

        for (int monsterId : notify.getLocalAvatarAlertedMonsterListList()) {
            // has_path_to_target is one of AiSyncInfo's two unnamed bools in the 7.0 dump and there
            // is no way to tell which, so it is left unset rather than guessed at.
            proto.addInfoList(AiSyncInfo.newBuilder().setEntityId(monsterId));
        }

        this.setData(proto);
    }
}
