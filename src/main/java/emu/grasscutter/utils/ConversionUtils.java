package emu.grasscutter.utils;

/* Various methods to convert from A -> B. */
public interface ConversionUtils {
    static long gameTimeToDays(long minutes) {
        return minutes / 1440;
    }

    static long gameTimeToHours(long minutes) {
        return minutes / 60;
    }
}
