package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.*;
import emu.grasscutter.net.proto.*;
import java.util.List;

public class PacketTakeFurnitureMakeRsp extends BasePacket {

    public PacketTakeFurnitureMakeRsp(
            int ret,
            int makeId,
            List<ItemParamOuterClass.ItemParam> output,
            List<FurnitureMakeDataOuterClass.FurnitureMakeData> others) {
        super(PacketOpcodes.TakeFurnitureMakeRsp);

        var proto = TakeFurnitureMakeRspOuterClass.TakeFurnitureMakeRsp.newBuilder();

        proto.setRetcode(ret).setMakeId(makeId);

        // output_item_list is deliberately not sent: 7.0 leaves two `repeated ItemParam` fields here
        // obfuscated (6 and 10) and 6.7 has two names for them, output_item_list and
        // return_item_list, with nothing to say which way round they go. The furniture is already
        // in the inventory, so this only costs the reward popup its contents.

        if (others != null) {
            proto.setFurnitureMakeSlot(
                    FurnitureMakeSlotOuterClass.FurnitureMakeSlot.newBuilder()
                            .addAllFurnitureMakeDataList(others)
                            .build());
        }

        this.setData(proto);
    }
}
