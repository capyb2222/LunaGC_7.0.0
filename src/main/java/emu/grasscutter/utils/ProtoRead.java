package emu.grasscutter.utils;

import java.nio.charset.StandardCharsets;

/**
 * Reads single fields out of a protobuf payload by number, without a schema.
 *
 * <p>A generated class cannot read a payload from a newer client: a field it declares as a string
 * may now carry a varint, and {@code parseFrom} throws on the mismatch rather than returning the
 * parts that still line up. During a version bring-up the field numbers are known one at a time,
 * from a live capture, long before there is a proto to regenerate from - so the reader has to take
 * the number as an argument instead of baking it in. {@link ProtoSniff} finds the numbers; this
 * reads the values once they are known.
 *
 * <p>Temporary by design. When a dump for the version exists the generated classes come back and
 * the handlers should go back with them.
 */
public final class ProtoRead {

    private ProtoRead() {}

    /** The varint at {@code field}, or 0 if it is absent - which is what an absent varint means. */
    public static long varint(byte[] data, int field) {
        var found = find(data, field, 0);
        return found == null ? 0 : (long) found;
    }

    /** The length-delimited field at {@code field} as UTF-8, or "" if absent. */
    public static String string(byte[] data, int field) {
        var found = find(data, field, 2);
        return found == null ? "" : new String((byte[]) found, StandardCharsets.UTF_8);
    }

    /** Walks the payload and returns the first value of {@code field}, or null. */
    private static Object find(byte[] data, int field, int wanted) {
        if (data == null) return null;
        int i = 0;
        while (i < data.length) {
            long[] kv;
            try {
                kv = varintAt(data, i);
            } catch (Exception e) {
                return null;
            }
            i = (int) kv[1];
            int number = (int) (kv[0] >>> 3);
            int wire = (int) (kv[0] & 7);
            if (number == 0) return null;
            try {
                switch (wire) {
                    case 0 -> {
                        long[] v = varintAt(data, i);
                        i = (int) v[1];
                        if (number == field && wanted == 0) return v[0];
                    }
                    case 1 -> i += 8;
                    case 2 -> {
                        long[] len = varintAt(data, i);
                        i = (int) len[1];
                        int n = (int) len[0];
                        if (n < 0 || i + n > data.length) return null;
                        if (number == field && wanted == 2) {
                            var out = new byte[n];
                            System.arraycopy(data, i, out, 0, n);
                            return out;
                        }
                        i += n;
                    }
                    case 5 -> i += 4;
                    default -> {
                        return null;
                    }
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    private static long[] varintAt(byte[] data, int pos) {
        long value = 0;
        int shift = 0;
        while (pos < data.length) {
            byte b = data[pos++];
            value |= (long) (b & 0x7f) << shift;
            if ((b & 0x80) == 0) return new long[] {value, pos};
            shift += 7;
            if (shift > 63) throw new IllegalStateException("varint too long");
        }
        throw new IllegalStateException("varint ran off the end");
    }
}
