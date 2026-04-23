package emu.grasscutter.server.packet.send;

import static emu.grasscutter.config.Configuration.INVENTORY_LIMITS;

import emu.grasscutter.net.packet.*;
import emu.grasscutter.net.proto.StoreTypeOuterClass.StoreType;
import emu.grasscutter.net.proto.StoreWeightLimitNotifyOuterClass.StoreWeightLimitNotify;

public class PacketStoreWeightLimitNotify extends BasePacket {

    public PacketStoreWeightLimitNotify() {
        super(PacketOpcodes.StoreWeightLimitNotify);

        StoreWeightLimitNotify p =
                StoreWeightLimitNotify.newBuilder()
                        .setStoreType(StoreType.StoreType_STORE_PACK)
                        .setWeightLimit(INVENTORY_LIMITS.all)
                        .setReliquaryCountLimit(INVENTORY_LIMITS.relics)
                        .setFurnitureCountLimit(INVENTORY_LIMITS.furniture)
                        .build();

        this.setData(p);
    }
}
