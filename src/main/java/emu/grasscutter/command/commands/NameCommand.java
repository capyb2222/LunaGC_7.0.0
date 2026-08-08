package emu.grasscutter.command.commands;

import static emu.grasscutter.utils.lang.Language.translate;

import emu.grasscutter.command.*;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.server.packet.send.PacketSetPlayerNameRsp;
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

        int start = parseColor(args.get(2));
        int end = parseColor(args.get(3));
        if (start < 0 || end < 0) {
            CommandHandler.sendMessage(sender, translate(sender, "commands.name.bad_color"));
            return;
        }

        apply(sender, targetPlayer, buildGradient(text, start, end));
    }

    /** Wraps each character in its own colour tag, stepping linearly from start to end. */
    private static String buildGradient(String text, int start, int end) {
        var sb = new StringBuilder();
        int last = text.length() - 1;

        for (int i = 0; i <= last; i++) {
            char c = text.charAt(i);
            // Tagging whitespace only bloats the string; it renders the same either way.
            if (Character.isWhitespace(c)) {
                sb.append(c);
                continue;
            }

            // A single-character name has no distance to interpolate over; use the start colour.
            float t = last == 0 ? 0f : (float) i / last;
            sb.append("<color=#").append(String.format("%06X", lerpColor(start, end, t))).append('>');
            sb.append(c);
            sb.append("</color>");
        }
        return sb.toString();
    }

    /** Interpolates two packed 0xRRGGBB values per channel. */
    private static int lerpColor(int from, int to, float t) {
        int r = Math.round(((from >> 16) & 0xFF) + (((to >> 16) & 0xFF) - ((from >> 16) & 0xFF)) * t);
        int g = Math.round(((from >> 8) & 0xFF) + (((to >> 8) & 0xFF) - ((from >> 8) & 0xFF)) * t);
        int b = Math.round((from & 0xFF) + ((to & 0xFF) - (from & 0xFF)) * t);
        return (r << 16) | (g << 8) | b;
    }

    /**
     * Accepts #RGB, #RRGGBB, or either without the leading '#'.
     *
     * @return the packed 0xRRGGBB value, or -1 if it could not be parsed.
     */
    private static int parseColor(String raw) {
        var hex = raw.startsWith("#") ? raw.substring(1) : raw;

        if (hex.length() == 3) {
            // Expand shorthand: F0A -> FF00AA
            var sb = new StringBuilder();
            for (char c : hex.toCharArray()) sb.append(c).append(c);
            hex = sb.toString();
        }

        if (hex.length() != 6) return -1;
        try {
            return Integer.parseInt(hex, 16);
        } catch (NumberFormatException ignored) {
            return -1;
        }
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
