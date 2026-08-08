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

        /*
         * The client uses positive mail IDs:
         *
         * client mail ID 1 -> internal list index 0
         * client mail ID 2 -> internal list index 1
         * client mail ID 3 -> internal list index 2
         *
         * PacketGetMailItemRsp currently expects LunaGC's internal
         * zero-based indexes, so convert the received IDs here.
         */
        List<Integer> internalIndexes =
                new ArrayList<>();

        for (int clientMailId :
                req.getMailIdListList()) {

            int internalIndex =
                    mailHandler.toInternalMailIndex(
                            clientMailId);

            /*
             * Ignore invalid IDs rather than allowing an invalid
             * List.get(index) call later.
             */
            if (internalIndex < 0) {
                continue;
            }

            /*
             * Do not process the same attachment twice if a malformed
             * request contains the same ID more than once.
             */
            if (!internalIndexes.contains(
                    internalIndex)) {

                internalIndexes.add(
                        internalIndex);
            }
        }

        /*
         * PacketGetMailItemRsp performs the actual claim operation:
         *
         * - retrieves each mail by internal index;
         * - grants the attachments;
         * - marks isAttachmentGot as true;
         * - saves the modified mail;
         * - builds GetMailItemRsp;
         * - sends MailChangeNotify.
         */
        session.send(
                new PacketGetMailItemRsp(
                        player,
                        internalIndexes));
    }
}