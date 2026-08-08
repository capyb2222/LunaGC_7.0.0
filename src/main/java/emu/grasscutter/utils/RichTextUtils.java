package emu.grasscutter.utils;

/**
 * Helpers for the rich-text markup the client renders in name and watermark fields.
 *
 * <p>Both {@code UnityEngine.UI.Text} (the beta watermark) and TextMeshPro (nicknames) accept
 * {@code <color=#RRGGBB>} tags, so the same markup drives both.
 */
public final class RichTextUtils {
    private RichTextUtils() {}

    /** Bytes a single-character colour tag costs: {@code <color=#RRGGBB>} + char + {@code </color>}. */
    public static final int BYTES_PER_GRADIENT_CHAR = 24;

    /**
     * Parses #RRGGBB, #RGB shorthand, or either without the leading '#'.
     *
     * @return the packed 0xRRGGBB value, or -1 if it could not be parsed.
     */
    public static int parseColor(String raw) {
        if (raw == null) return -1;
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

    /** Interpolates two packed 0xRRGGBB values per channel. */
    public static int lerpColor(int from, int to, float t) {
        int r = Math.round(((from >> 16) & 0xFF) + (((to >> 16) & 0xFF) - ((from >> 16) & 0xFF)) * t);
        int g = Math.round(((from >> 8) & 0xFF) + (((to >> 8) & 0xFF) - ((from >> 8) & 0xFF)) * t);
        int b = Math.round((from & 0xFF) + ((to & 0xFF) - (from & 0xFF)) * t);
        return (r << 16) | (g << 8) | b;
    }

    /** Wraps the whole string in one colour tag. Far cheaper than a gradient. */
    public static String colorize(String text, int color) {
        return "<color=#" + String.format("%06X", color) + ">" + text + "</color>";
    }

    /** Wraps each character in its own colour tag, stepping linearly from start to end. */
    public static String gradient(String text, int start, int end) {
        var sb = new StringBuilder();
        int last = text.length() - 1;

        for (int i = 0; i <= last; i++) {
            char c = text.charAt(i);
            // Tagging whitespace only bloats the string; it renders the same either way.
            if (Character.isWhitespace(c)) {
                sb.append(c);
                continue;
            }

            // A single-character string has no distance to interpolate over; use the start colour.
            float t = last == 0 ? 0f : (float) i / last;
            sb.append(colorize(String.valueOf(c), lerpColor(start, end, t)));
        }
        return sb.toString();
    }
}
