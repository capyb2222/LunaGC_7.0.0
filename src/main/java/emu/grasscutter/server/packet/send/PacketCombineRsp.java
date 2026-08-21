package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.*;
import emu.grasscutter.net.proto.*;

public class PacketCombineRsp extends BasePacket {

    public PacketCombineRsp() {
        super(PacketOpcodes.CombineRsp);

        CombineRspOuterClass.CombineRsp proto =
                CombineRspOuterClass.CombineRsp.newBuilder()
                        .setRetcode(RetcodeOuterClass.Retcode.RET_SVR_ERROR_VALUE)
                        .build();

        this.setData(proto);
    }

    public PacketCombineRsp(int retcode) {
        super(PacketOpcodes.CombineRsp);

        CombineRspOuterClass.CombineRsp proto =
                CombineRspOuterClass.CombineRsp.newBuilder().setRetcode(retcode).build();

        this.setData(proto);
    }

    public PacketCombineRsp(
            CombineReqOuterClass.CombineReq combineReq,
            Iterable<ItemParamOuterClass.ItemParam> costItemList,
            Iterable<ItemParamOuterClass.ItemParam> resultItemList,
            Iterable<ItemParamOuterClass.ItemParam> totalRandomItemList,
            Iterable<ItemParamOuterClass.ItemParam> totalReturnItemList,
            Iterable<ItemParamOuterClass.ItemParam> totalExtraItemList) {

        super(PacketOpcodes.CombineRsp);

        CombineRspOuterClass.CombineRsp proto =
                CombineRspOuterClass.CombineRsp.newBuilder()
                        .setRetcode(RetcodeOuterClass.Retcode.RET_SUCC_VALUE)
                        .setCombineId(combineReq.getCombineId())
                        .setCombineCount(combineReq.getCombineCount())
                        .setAvatarGuid(combineReq.getAvatarGuid())
                        .addAllCostItemList(costItemList)
                        .addAllResultItemList(resultItemList)
                        .addAllTotalExtraItemList(totalExtraItemList)
                        // totalRandomItemList and totalReturnItemList are deliberately not sent.
                        // 7.0 leaves three `repeated ItemParam` fields here obfuscated (2, 3 and 4)
                        // and 6.7 has exactly three to match them to - these two plus uk10 - with
                        // nothing to say which is which. Guessing would put items under the wrong
                        // field; sending them at their 6.7 numbers, which is what happened before,
                        // wrote a length-delimited value into 7.0's combine_id and made the whole
                        // response unparseable. The craft itself is unaffected either way: the
                        // items are already in the inventory by the time this is built.
                        .build();

        this.setData(proto);
    }
}
