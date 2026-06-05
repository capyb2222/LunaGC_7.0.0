package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.*;
import emu.grasscutter.net.proto.*;
import emu.grasscutter.net.proto.RetcodeOuterClass.Retcode;

public class PacketBuyGoodsRsp extends BasePacket {
    public PacketBuyGoodsRsp(int shopType, int boughtNum, ShopGoodsOuterClass.ShopGoods sg) {
        super(PacketOpcodes.BuyGoodsRsp);

        BuyGoodsRspOuterClass.BuyGoodsRsp buyGoodsRsp =
                BuyGoodsRspOuterClass.BuyGoodsRsp.newBuilder()
                        .setShopType(shopType)
                        .setBuyCount(boughtNum)
                        .addGoodsList(
                                ShopGoodsOuterClass.ShopGoods.newBuilder().mergeFrom(sg).setBoughtNum(boughtNum))
                        .build();

        this.setData(buyGoodsRsp);
    }

    public PacketBuyGoodsRsp(Retcode retcode) {
        super(PacketOpcodes.BuyGoodsRsp);
        this.setData(
                BuyGoodsRspOuterClass.BuyGoodsRsp.newBuilder()
                        .setRetcode(retcode.getNumber())
                        .build());
    }
}
