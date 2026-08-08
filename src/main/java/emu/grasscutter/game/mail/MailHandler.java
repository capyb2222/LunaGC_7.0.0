package emu.grasscutter.game.mail;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.database.DatabaseHelper;
import emu.grasscutter.game.player.*;
import emu.grasscutter.server.event.player.PlayerReceiveMailEvent;
import emu.grasscutter.server.packet.send.*;
import java.util.*;

public class MailHandler extends BasePlayerManager {
    private final List<Mail> mail;

    public MailHandler(Player player) {
        super(player);

        this.mail = new ArrayList<>();
    }

    public List<Mail> getMail() {
        return mail;
    }

    // ---------------------MAIL------------------------

    public void sendMail(Mail message) {
        // Call mail receive event.
        PlayerReceiveMailEvent event = new PlayerReceiveMailEvent(this.getPlayer(), message);
        event.call();
        if (event.isCanceled()) return;
        message = event.getMessage();

        message.setOwnerUid(this.getPlayer().getUid());
        message.save();

        this.mail.add(message);

        Grasscutter.getLogger()
                .debug(
                        "Mail sent to user ["
                                + this.getPlayer().getUid()
                                + ":"
                                + this.getPlayer().getNickname()
                                + "]!");

        if (this.getPlayer().isOnline()) {
            this.getPlayer().sendPacket(new PacketMailChangeNotify(this.getPlayer(), message));
        } // TODO: setup a way for the mail notification to show up when someone receives mail when they
        // were offline
    }

    public boolean deleteMail(int mailId) {
        Mail message = getMailById(mailId);

        if (message != null) {
            this.getMail().remove(mailId);
            message.expireTime = 0;
            message.save();

            return true;
        }

        return false;
    }

	public void deleteMail(List<Integer> internalIndexes) {
		/*
		 * Remove duplicates before deleting. Deleting the same index twice
		 * could otherwise remove a different mail after the list shifts.
		 */
		List<Integer> sortedInternalIndexes =
				new ArrayList<>(
						new LinkedHashSet<>(internalIndexes));

		/*
		 * Delete from highest index to lowest index so earlier removals do
		 * not shift the indexes that still need to be processed.
		 */
		sortedInternalIndexes.sort(
				Collections.reverseOrder());

		List<Integer> deletedClientIds =
				new ArrayList<>();

		for (int internalIndex : sortedInternalIndexes) {
			/*
			 * Save the client-visible ID before removing the mail.
			 */
			int clientMailId =
					this.toClientMailId(internalIndex);

			if (this.deleteMail(internalIndex)) {
				deletedClientIds.add(clientMailId);
			}
		}

		player.getSession().send(
				new PacketDelMailRsp(
						player,
						deletedClientIds));

		player.getSession().send(
				new PacketMailChangeNotify(
						player,
						null,
						deletedClientIds));
	}

	public Mail getMailById(int index) {
		if (index < 0 || index >= this.mail.size()) {
			return null;
		}

		return this.mail.get(index);
	}
	
	/**
	 * Converts LunaGC's internal zero-based list index into the positive
	 * mail ID sent to the client.
	 *
	 * Internal index 0 -> client ID 1
	 * Internal index 1 -> client ID 2
	 */
	public int toClientMailId(int internalIndex) {
		if (internalIndex < 0) {
			return 0;
		}

		return internalIndex + 1;
	}

	/**
	 * Converts a positive client mail ID back into LunaGC's internal
	 * zero-based list index.
	 *
	 * Client ID 1 -> internal index 0
	 * Client ID 2 -> internal index 1
	 *
	 * Returns -1 when the supplied client ID is invalid.
	 */
	public int toInternalMailIndex(int clientMailId) {
		int internalIndex = clientMailId - 1;

		if (internalIndex < 0
				|| internalIndex >= this.mail.size()) {
			return -1;
		}

		return internalIndex;
	}

    public int getMailIndex(Mail message) {
        return this.mail.indexOf(message);
    }

    public boolean replaceMailByIndex(int index, Mail message) {
        if (getMailById(index) != null) {
            this.mail.set(index, message);
            message.save();
            return true;
        } else {
            return false;
        }
    }

    public void loadFromDatabase() {
        List<Mail> mailList = DatabaseHelper.getAllMail(this.getPlayer());

        for (Mail mail : mailList) {
            this.getMail().add(mail);
        }
    }
}
