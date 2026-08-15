package emu.grasscutter.server.packet.send;

import emu.grasscutter.data.common.ItemParamData;
import emu.grasscutter.net.packet.*;
import emu.grasscutter.net.proto.ItemParamOuterClass.ItemParam;
import emu.grasscutter.net.proto._TakeDailyTaskScoreRewardRspOuterClass._TakeDailyTaskScoreRewardRsp;
import java.util.List;

public class PacketTakeDailyTaskScoreRewardRsp extends BasePacket {

    public PacketTakeDailyTaskScoreRewardRsp(int retcode, boolean isClaimDailyAttendance) {
        this(retcode, isClaimDailyAttendance, List.of());
    }

    public PacketTakeDailyTaskScoreRewardRsp(
            int retcode, boolean isClaimDailyAttendance, List<ItemParamData> items) {
        super(PacketOpcodes.TakeDailyTaskScoreRewardRsp);

        var proto =
                _TakeDailyTaskScoreRewardRsp.newBuilder()
                        .setRetcode(retcode)
                        // Echoed rather than decided here: the client sets it to say which screen it
                        // is claiming from, and the reply is about the same claim.
                        .setIsClaimDailyAttendance(isClaimDailyAttendance);

        for (ItemParamData item : items) {
            proto.addItemList(
                    ItemParam.newBuilder().setItemId(item.getId()).setCount(item.getCount()).build());
        }

        this.setData(proto.build());
    }
}
