package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.*;
import emu.grasscutter.net.proto.GachaWishRspOuterClass.GachaWishRsp;
import emu.grasscutter.net.proto.RetcodeOuterClass.Retcode;

public class PacketGachaWishRsp extends BasePacket {

    public PacketGachaWishRsp(Retcode retcode) {
        super(PacketOpcodes.GachaWishRsp);

        this.setData(GachaWishRsp.newBuilder().setRetcode(retcode.getNumber()).build());
    }

    public PacketGachaWishRsp(
            int gachaType, int scheduleId, int itemId, int progress, int maxProgress) {
        super(PacketOpcodes.GachaWishRsp);

        GachaWishRsp proto =
                GachaWishRsp.newBuilder()
                        .setGachaType(gachaType)
                        .setGachaScheduleId(scheduleId)
                        .setWishItemId(itemId)
                        .setWishProgress(progress)
                        .setWishMaxProgress(maxProgress)
                        .build();

        this.setData(proto);
    }
}
