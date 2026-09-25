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

            if (message.isRead) {
                continue;
            }

            message.isRead = true;

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