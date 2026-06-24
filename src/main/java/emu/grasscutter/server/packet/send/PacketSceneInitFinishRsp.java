package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.*;
import emu.grasscutter.net.proto.SceneInitFinishRspOuterClass.SceneInitFinishRsp;

public class PacketSceneInitFinishRsp extends BasePacket {

    public PacketSceneInitFinishRsp(Player player) {
        super(PacketOpcodes.SceneInitFinishRsp, 11);

        int maskedToken = (player.getEnterSceneToken() - 18277) ^ 51147;
        int maskedRetcode = (0 - 42120) ^ 30000;

        SceneInitFinishRsp p =
                SceneInitFinishRsp.newBuilder()
                        .setEnterSceneToken(maskedToken)
                        .setRetcode(maskedRetcode)
                        .build();

        this.setData(p.toByteArray());
    }
}
