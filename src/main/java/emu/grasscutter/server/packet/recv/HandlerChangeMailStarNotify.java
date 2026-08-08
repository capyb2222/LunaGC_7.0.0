package emu.grasscutter.server.packet.recv;

import emu.grasscutter.game.mail.Mail;
import emu.grasscutter.net.packet.Opcodes;
import emu.grasscutter.net.packet.PacketHandler;
import emu.grasscutter.net.packet.PacketOpcodes;
import emu.grasscutter.net.proto.ChangeMailStarNotifyOuterClass.ChangeMailStarNotify;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketMailChangeNotify;

import java.util.ArrayList;
import java.util.List;

@Opcodes(PacketOpcodes.ChangeMailStarNotify)
public class HandlerChangeMailStarNotify extends PacketHandler {

    @Override
    public void handle(
            GameSession session,
            byte[] header,
            byte[] payload)
            throws Exception {

        ChangeMailStarNotify req =
                ChangeMailStarNotify.parseFrom(
                        payload);

        var player =
                session.getPlayer();

        var mailHandler =
                player.getMailHandler();

        List<Mail> updatedMail =
                new ArrayList<>();

        /*
         * LunaGC stores starred mail as importance = 1 and unstarred
         * mail as importance = 0.
         */
        int requestedImportance =
                req.getIsStar() ? 1 : 0;

        for (int clientMailId :
                req.getMailIdListList()) {

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
             * Avoid an unnecessary database write when the mail already
             * has the requested star state.
             */
            if (message.importance
                    == requestedImportance) {

                continue;
            }

            message.importance =
                    requestedImportance;

            if (player.replaceMailByIndex(
                    internalIndex,
                    message)) {

                updatedMail.add(
                        message);
            }
        }

        if (!updatedMail.isEmpty()) {
            session.send(
                    new PacketMailChangeNotify(
                            player,
                            updatedMail));
        }
    }
}