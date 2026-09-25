package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.*;
import emu.grasscutter.net.proto.CompoundQueueDataOuterClass.CompoundQueueData;
import emu.grasscutter.net.proto.PlayerCompoundMaterialRspOuterClass.PlayerCompoundMaterialRsp;
import emu.grasscutter.net.proto.RetcodeOuterClass.Retcode;

public class PacketPlayerCompoundMaterialRsp extends BasePacket {
    public PacketPlayerCompoundMaterialRsp(CompoundQueueData compoundQueueData) {
        super(PacketOpcodes.PlayerCompoundMaterialRsp);
        PlayerCompoundMaterialRsp proto =
                PlayerCompoundMaterialRsp.newBuilder()
                        .setCompoundQueData(compoundQueueData)
                        .setRetcode(Retcode.RET_SUCC_VALUE)
                        .build();
        setData(proto);
    }

    public PacketPlayerCompoundMaterialRsp(int retcode) {
        super(PacketOpcodes.PlayerCompoundMaterialRsp);
        PlayerCompoundMaterialRsp proto =
                PlayerCompoundMaterialRsp.newBuilder().setRetcode(retcode).build();
        setData(proto);
    }
}
