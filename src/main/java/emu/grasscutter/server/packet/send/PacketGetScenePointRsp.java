package emu.grasscutter.server.packet.send;

import emu.grasscutter.data.GameData;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.*;
import emu.grasscutter.net.proto.GetScenePointRspOuterClass.GetScenePointRsp;

public class PacketGetScenePointRsp extends BasePacket {

    public PacketGetScenePointRsp(Player player, int sceneId) {
        super(PacketOpcodes.GetScenePointRsp);

        GetScenePointRsp.Builder p = GetScenePointRsp.newBuilder().setSceneId(sceneId);

        var unlockedPoints = player.getUnlockedScenePoints(sceneId);
        if (GameData.getScenePointIdList().size() == 0 || unlockedPoints.isEmpty()) {
            for (int i = 1; i < 1000; i++) {
                p.addUnlockedPointList(i);
                p.addUnhidePointList(i);
            }
        } else {
            p.addAllUnlockedPointList(unlockedPoints);
            p.addAllUnhidePointList(unlockedPoints);
        }

        for (int i = 1; i < 9; i++) {
            p.addUnlockAreaList(i);
        }

        this.setData(p);
    }
}
