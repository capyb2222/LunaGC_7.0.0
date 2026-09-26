package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.world.WorldRegions;
import emu.grasscutter.net.packet.*;
import emu.grasscutter.net.proto.SceneDataNotifyOuterClass.SceneDataNotify;

public class PacketSceneDataNotify extends BasePacket {

    public PacketSceneDataNotify(int sceneId) {
        super(PacketOpcodes.SceneDataNotify);

        var proto = SceneDataNotify.newBuilder().setSceneId(sceneId);
        var regions = WorldRegions.openRegions(sceneId);
        if (regions.getLimitedRegionListCount() > 0) proto.setLimitedRegionInfo(regions);

        this.setData(proto);
    }
}
