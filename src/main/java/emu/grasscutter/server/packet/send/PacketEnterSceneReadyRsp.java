package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.*;
import emu.grasscutter.net.proto.EnterSceneReadyRspOuterClass.EnterSceneReadyRsp;

public class PacketEnterSceneReadyRsp extends BasePacket {

    public PacketEnterSceneReadyRsp(Player player) {
        super(PacketOpcodes.EnterSceneReadyRsp, 11);

        int maskedToken = (player.getEnterSceneToken() + 57396) ^ 8638;

        EnterSceneReadyRsp p =
                EnterSceneReadyRsp.newBuilder().setEnterSceneToken(maskedToken).build();

        this.setData(p.toByteArray());
    }
}
