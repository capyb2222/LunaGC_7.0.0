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

        /*
         * The client sends positive IDs:
         *
         * client ID 1 -> internal list index 0
         * client ID 2 -> internal list index 1
         *
         * MailHandler.deleteMail(List) expects internal indexes.
         */
        List<Integer> internalIndexes =
                new ArrayList<>();

        for (int clientMailId :
                req.getMailIdListList()) {

            int internalIndex =
                    mailHandler.toInternalMailIndex(
                            clientMailId);

            /*
             * Ignore invalid IDs instead of allowing an invalid
             * List.get(index) operation.
             */
            if (internalIndex < 0) {
                continue;
            }

            /*
             * Avoid processing the same mail twice if the client
             * unexpectedly sends a duplicate ID.
             */
            if (!internalIndexes.contains(
                    internalIndex)) {

                internalIndexes.add(
                        internalIndex);
            }
        }

        /*
         * MailHandler performs the deletion and sends:
         *
         * - PacketDelMailRsp
         * - PacketMailChangeNotify
         *
         * Do not send those packets again from this handler.
         */
        mailHandler.deleteMail(
                internalIndexes);
    }
}