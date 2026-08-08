package emu.grasscutter.server.packet.recv;

import emu.grasscutter.game.mail.Mail;
import emu.grasscutter.net.packet.Opcodes;
import emu.grasscutter.net.packet.PacketHandler;
import emu.grasscutter.net.packet.PacketOpcodes;
import emu.grasscutter.net.proto.ReadMailNotifyOuterClass.ReadMailNotify;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketMailChangeNotify;

import java.util.ArrayList;
import java.util.List;

@Opcodes(PacketOpcodes.ReadMailNotify)
public class HandlerReadMailNotify extends PacketHandler {

    @Override
    public void handle(
            GameSession session,
            byte[] header,
            byte[] payload)
            throws Exception {

        ReadMailNotify req =
                ReadMailNotify.parseFrom(payload);

        var player =
                session.getPlayer();

        var mailHandler =
                player.getMailHandler();

        List<Mail> updatedMail =
                new ArrayList<>();

        for (int clientMailId :
                req.getMailIdListList()) {

            /*
             * Convert the ID shown to the client into the index used by
             * LunaGC's internal List<Mail>.
             */
            int internalIndex =
                    mailHandler.toInternalMailIndex(
                            clientMailId);

            if (internalIndex < 0) {
                continue;
            }

            Mail message =
                    player.getMail(
                            internalIndex);

            if (message == null) {
                continue;
            }

            /*
             * There is no need to rewrite and save mail that is already
             * marked as read.
             */
            if (message.isRead) {
                continue;
            }

            message.isRead = true;

            /*
             * replaceMailByIndex expects the internal zero-based index.
             * It also saves the modified Mail document.
             */
            if (player.replaceMailByIndex(
                    internalIndex,
                    message)) {

                updatedMail.add(
                        message);
            }
        }

        /*
         * PacketMailChangeNotify's List<Mail> constructor should place
         * these entries in change_mail_list, because they already exist
         * in the client's mailbox.
         */
        if (!updatedMail.isEmpty()) {
            session.send(
                    new PacketMailChangeNotify(
                            player,
                            updatedMail));
        }
    }
}