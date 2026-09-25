package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.Opcodes;
import emu.grasscutter.net.packet.PacketHandler;
import emu.grasscutter.net.packet.PacketOpcodes;
import emu.grasscutter.net.proto.DelMailReqOuterClass.DelMailReq;
import emu.grasscutter.server.game.GameSession;

import java.util.ArrayList;
import java.util.List;

@Opcodes(PacketOpcodes.DelMailReq)
public class HandlerDelMailReq extends PacketHandler {

    @Override
    public void handle(
            GameSession session,
            byte[] header,
            byte[] payload)
            throws Exception {

        DelMailReq req =
                DelMailReq.parseFrom(payload);

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

        mailHandler.deleteMail(
                internalIndexes);
    }
}