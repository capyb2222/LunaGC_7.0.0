package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.Opcodes;
import emu.grasscutter.net.packet.PacketHandler;
import emu.grasscutter.net.packet.PacketOpcodes;
import emu.grasscutter.net.proto.GetMailItemReqOuterClass.GetMailItemReq;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketGetMailItemRsp;

import java.util.ArrayList;
import java.util.List;

@Opcodes(PacketOpcodes.GetMailItemReq)
public class HandlerGetMailItemReq extends PacketHandler {

    @Override
    public void handle(
            GameSession session,
            byte[] header,
            byte[] payload)
            throws Exception {

        GetMailItemReq req =
                GetMailItemReq.parseFrom(payload);

        var player =
                session.getPlayer();

        var mailHandler =
                player.getMailHandler();

        List<Integer> internalIndexes =
                new ArrayList<>();

        for (int clientMailId :
                req.getMailIdListList()) {

            int internalIndex =
                    mailHandler.toInternalMailIndex(
                            clientMailId);

            if (internalIndex < 0) {
                continue;
            }

            if (!internalIndexes.contains(
                    internalIndex)) {

                internalIndexes.add(
                        internalIndex);
            }
        }

        session.send(
                new PacketGetMailItemRsp(
                        player,
                        internalIndexes));
    }
}