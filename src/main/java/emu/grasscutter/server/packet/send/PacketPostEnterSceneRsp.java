package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.*;
import emu.grasscutter.net.proto.PostEnterSceneRspOuterClass.PostEnterSceneRsp;

public class PacketPostEnterSceneRsp extends BasePacket {

    public PacketPostEnterSceneRsp(Player player) {
        super(PacketOpcodes.PostEnterSceneRsp);

        int maskedToken = (player.getEnterSceneToken() ^ 59003) + 18565;

        player.unfreezeUnlockedScenePoints();

        this.setData(PostEnterSceneRsp.newBuilder()
            .setEnterSceneToken(maskedToken)
            .build());
    }
}
