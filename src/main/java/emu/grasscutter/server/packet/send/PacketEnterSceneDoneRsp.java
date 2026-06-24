package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.*;
import emu.grasscutter.net.proto.EnterSceneDoneRspOuterClass.EnterSceneDoneRsp;

public class PacketEnterSceneDoneRsp extends BasePacket {

    public PacketEnterSceneDoneRsp(Player player) {
        super(PacketOpcodes.EnterSceneDoneRsp);

        int maskedToken = (player.getEnterSceneToken() ^ 49009) - 34315;
        int maskedRetcode = (0 ^ 27936) - 48812;

        this.setData(EnterSceneDoneRsp.newBuilder()
            .setEnterSceneToken(maskedToken)
            .setRetcode(maskedRetcode)
            .build());
    }
}
