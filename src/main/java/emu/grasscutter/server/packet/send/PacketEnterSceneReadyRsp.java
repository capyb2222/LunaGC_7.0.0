package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.*;
import emu.grasscutter.net.proto.EnterSceneReadyRspOuterClass.EnterSceneReadyRsp;

public class PacketEnterSceneReadyRsp extends BasePacket {

    public PacketEnterSceneReadyRsp(Player player) {
        super(PacketOpcodes.EnterSceneReadyRsp, 11);

        int maskedToken = (player.getEnterSceneToken() + 57396) ^ 8638;
        int maskedRetcode = (0 ^ 33726) + 4215;

        EnterSceneReadyRsp p =
                EnterSceneReadyRsp.newBuilder()
                        .setEnterSceneToken(maskedToken)
                        .setRetcode(maskedRetcode)
                        .build();

        this.setData(p.toByteArray());
    }
}
