package emu.grasscutter.game.mail;

import static emu.grasscutter.config.Configuration.GAME_OPTIONS;

import emu.grasscutter.config.ConfigContainer.GameOptions.BirthdayMailOptions;
import emu.grasscutter.config.ConfigContainer.GameOptions.BirthdayMailOptions.GiftItem;
import emu.grasscutter.game.mail.Mail.MailContent;
import emu.grasscutter.game.mail.Mail.MailItem;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.utils.Utils;
import emu.grasscutter.utils.lang.Language;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * Sends a player a mail with a small gift on their in-game birthday.
 *
 * <p>This is based on Grasscutter PR #2549 ("Added Traveler's birthday gift system"), adapted to
 * LunaGC's own systems:
 *
 * <ul>
 *   <li>Uses the {@link emu.grasscutter.game.player.PlayerBirthday} field that LunaGC already
 *       stores on {@link Player}, instead of stuffing the date into player properties.
 *   <li>Uses {@link Language#translate(Player, String, Object...)} so the mail is shown in each
 *       player's own configured language, instead of a hardcoded English string.
 *   <li>Reads the gift items, mail expiry, and an enable/disable switch from {@link
 *       BirthdayMailOptions} instead of hardcoding them, so server owners can configure or turn
 *       off the feature. The mail is sent from "Server", matching the convention already used by
 *       {@link emu.grasscutter.command.commands.SendMailCommand}.
 *   <li>Tracks the last year a player was mailed (see {@link Player#getLastBirthdayMailYear()}) so
 *       the mail is only ever sent once per birthday, no matter how many times the player logs in
 *       that day.
 * </ul>
 */
public final class BirthdayMailSystem {
    private BirthdayMailSystem() {}
	
	/**
	 * Checks birthday mail using the server machine's current local date.
	 */
	public static void checkAndSend(Player player) {
		checkAndSend(player, LocalDate.now(ZoneId.systemDefault()));
	}	

    /**
     * Checks whether the player's birthday is today and, if so, sends them a gift mail - but only
     * once per year.
     *
     * @param player The player to check.
     * @param today The current date, in the server's local time zone.
     */
    public static void checkAndSend(Player player, LocalDate today) {
        var options = GAME_OPTIONS.birthdayMail;
        if (!options.enabled) return;

        var birthday = player.getBirthday();
        if (birthday == null || birthday.getDay() <= 0) return;
        if (birthday.getMonth() != today.getMonthValue() || birthday.getDay() != today.getDayOfMonth())
            return;

        // Don't send the mail more than once per year (e.g. multiple logins on the same day).
        if (player.getLastBirthdayMailYear() >= today.getYear()) return;

        List<MailItem> gifts = new ArrayList<>();
        for (GiftItem gift : options.gifts) {
            gifts.add(new MailItem(gift.itemId, gift.count));
        }

        var content =
                new MailContent(
                        Language.translate(player, "mail.birthday.title"),
                        Language.translate(player, "mail.birthday.content", player.getNickname()));

        long expireTime = Utils.getCurrentSeconds() + (options.expireDays * 86400L);
        var mail = new Mail(content, gifts, expireTime);

        player.getMailHandler().sendMail(mail);

        player.setLastBirthdayMailYear(today.getYear());
        player.save();
    }
}