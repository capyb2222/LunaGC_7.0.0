package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.*;
import emu.grasscutter.net.proto.TowerLevelStarCondDataOuterClass.TowerLevelStarCondData;
import emu.grasscutter.net.proto.TowerLevelStarCondNotifyOuterClass.TowerLevelStarCondNotify;

public class PacketTowerLevelStarCondNotify extends BasePacket {

    public PacketTowerLevelStarCondNotify(int floorId, int levelIndex, int lostStar) {
        super(PacketOpcodes.TowerLevelStarCondNotify);

        var proto = TowerLevelStarCondNotify.newBuilder().setFloorId(floorId).setLevelIndex(levelIndex);

        if (1 <= lostStar && lostStar <= 3) {
            proto.addCondDataList(
                    TowerLevelStarCondData.newBuilder()
                            .setCondValue(lostStar)
                            .setIsFail(true)
                            .setIsPause(true)
                            .setStarCondIndex(lostStar)
                            .build());
        } else {
            proto
                    .addCondDataList(TowerLevelStarCondData.newBuilder().build())
                    .addCondDataList(TowerLevelStarCondData.newBuilder().build())
                    .addCondDataList(TowerLevelStarCondData.newBuilder().build());
        }

        this.setData(proto.build());
    }
}
