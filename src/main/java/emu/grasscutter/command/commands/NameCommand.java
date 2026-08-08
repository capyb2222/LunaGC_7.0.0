package emu.grasscutter.command.commands;

import static emu.grasscutter.utils.lang.Language.translate;

import emu.grasscutter.command.*;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.server.packet.send.PacketSetPlayerNameRsp;
import emu.grasscutter.utils.RichTextUtils;
import java.util.List;

/**
 * Sets a player's nickname server-side, bypassing the client rename UI.
 *
 * <p>The client renders TextMeshPro rich text in name fields, so a nickname may contain markup such
 * as {@code <color=#0080FF>}. The in-game rename box will not accept those characters, and the
 * length a gradient needs (roughly 28 characters of markup per visible character) is far past what
 * it allows, so this exists to set the value directly.
 */
@Command(
        label = "name",
        usage = {
            "<text>",
            "gradient <text> <#startColor> <#endColor>",
            "uid",
            "reset"
        },
        aliases = {"nickname", "rename"},
        permission = "player.name",
        permissionTargeted = "player.name.others")
public final class NameCommand implements CommandHandler {
    private static final String DEFAULT_NICKNAME = "Traveler";

    /**
     * Visible characters allowed in a gradient. Each one costs ~28 characters of markup, and the
     * nickname is echoed in the friend list, co-op and chat, so this keeps the stored value sane.
     */
    private static final int MAX_GRADIENT_LENGTH = 32;

    /**
     * Hard cap on the stored value. A full 32-character gradient lands near 900, so this leaves room
     * for hand-written markup while keeping an unbounded string out of every profile packet.
     */
    private static final int MAX_STORED_LENGTH = 1024;

    @Override
    public void execute(Player sender, Player targetPlayer, List<String> args) {
        if (args.isEmpty()) {
            sendUsageMessage(sender);
            return;
        }

        switch (args.get(0).toLowerCase()) {
            case "reset" -> apply(sender, targetPlayer, DEFAULT_NICKNAME);
            case "uid" -> apply(sender, targetPlayer, String.valueOf(targetPlayer.getUid()));
            case "gradient" -> gradient(sender, targetPlayer, args);
            default -> apply(sender, targetPlayer, String.join(" ", args));
        }
    }

    private void gradient(Player sender, Player targetPlayer, List<String> args) {
        // gradient <text> <start> <end>
        if (args.size() != 4) {
            CommandHandler.sendMessage(sender, translate(sender, "commands.name.gradient_usage"));
            return;
        }

        var text = args.get(1);
        // "uid" is the common case for this, so let it stand in for the digits.
        if (text.equalsIgnoreCase("uid")) {
            text = String.valueOf(targetPlayer.getUid());
        }

        if (text.length() > MAX_GRADIENT_LENGTH) {
            CommandHandler.sendMessage(
                    sender, translate(sender, "commands.name.too_long", MAX_GRADIENT_LENGTH));
            return;
        }

        int start = RichTextUtils.parseColor(args.get(2));
        int end = RichTextUtils.parseColor(args.get(3));
        if (start < 0 || end < 0) {
            CommandHandler.sendMessage(sender, translate(sender, "commands.name.bad_color"));
            return;
        }

        apply(sender, targetPlayer, RichTextUtils.gradient(text, start, end));
    }




    private void apply(Player sender, Player targetPlayer, String nickname) {
        if (nickname.isBlank()) {
            sendUsageMessage(sender);
            return;
        }

        if (nickname.length() > MAX_STORED_LENGTH) {
            CommandHandler.sendMessage(
                    sender, translate(sender, "commands.name.too_long", MAX_STORED_LENGTH));
            return;
        }

        targetPlayer.setNickname(nickname);
        targetPlayer.save();
        // Echo the new value so the client refreshes without a relog.
        targetPlayer.sendPacket(new PacketSetPlayerNameRsp(targetPlayer));

        CommandHandler.sendMessage(
                sender, translate(sender, "commands.name.success", nickname.length()));
    }
}
