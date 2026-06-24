package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.*;
import emu.grasscutter.net.proto.PostEnterSceneRspOuterClass.PostEnterSceneRsp;

public class PacketPostEnterSceneRsp extends BasePacket {

    public PacketPostEnterSceneRsp(Player player) {
        super(PacketOpcodes.PostEnterSceneRsp);

        int maskedToken = (player.getEnterSceneToken() ^ 59003) + 18565;
        int maskedRetcode = (0 - 3964) ^ 29623;

        player.unfreezeUnlockedScenePoints();

        this.setData(PostEnterSceneRsp.newBuilder()
            .setEnterSceneToken(maskedToken)
            .setRetcode(maskedRetcode)
            .build());
    }
}
