package emu.grasscutter.utils;

import static emu.grasscutter.utils.FileUtils.getResourcePath;
import static emu.grasscutter.utils.lang.Language.translate;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.config.ConfigContainer;
import emu.grasscutter.data.DataLoader;
import emu.grasscutter.game.world.Position;
import emu.grasscutter.utils.objects.Returnable;
import io.javalin.http.Context;
import io.netty.buffer.*;
import it.unimi.dsi.fastutil.ints.*;
import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import javax.annotation.Nullable;
import org.slf4j.Logger;

@SuppressWarnings({"UnusedReturnValue", "BooleanMethodIsAlwaysInverted"})
public final class Utils {
    public static final Random random = new Random();
    private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();

    public static int randomRange(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    public static float randomFloatRange(float min, float max) {
        return random.nextFloat() * (max - min) + min;
    }

    public static double getDist(Position pos1, Position pos2) {
        double xs = pos1.getX() - pos2.getX();
        xs = xs * xs;

        double ys = pos1.getY() - pos2.getY();
        ys = ys * ys;

        double zs = pos1.getZ() - pos2.getZ();
        zs = zs * zs;

        return Math.sqrt(xs + zs + ys);
    }

    public static int getCurrentSeconds() {
        return (int) (System.currentTimeMillis() / 1000.0);
    }

    public static String lowerCaseFirstChar(String s) {
        StringBuilder sb = new StringBuilder(s);
        sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
        return sb.toString();
    }

    public static String toString(InputStream inputStream) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(inputStream);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        for (int result = bis.read(); result != -1; result = bis.read()) {
            buf.write((byte) result);
        }
        return buf.toString();
    }

    public static void logByteArray(byte[] array) {
        ByteBuf b = Unpooled.wrappedBuffer(array);
        Grasscutter.getLogger().info("\n" + ByteBufUtil.prettyHexDump(b));
        b.release();
    }

    public static String bytesToHex(byte[] bytes) {
        if (bytes == null) return "";
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String bytesToHex(ByteBuf buf) {
        return bytesToHex(byteBufToArray(buf));
    }

    public static byte[] byteBufToArray(ByteBuf buf) {
        byte[] bytes = new byte[buf.readableBytes()];
        buf.getBytes(buf.readerIndex(), bytes);
        return bytes;
    }

    public static int animatorHash(String name) {
        int h = 5381;
        for (char c : name.toCharArray()) {
            h = ((h << 5) + h) ^ (int) c;
        }
        return h;
    }

    public static int abilityHash(String str) {
        int v7 = 0;
        int v8 = 0;
        while (v8 < str.length()) {
            v7 = str.charAt(v8++) + 131 * v7;
        }
        return v7;
    }

    public static String toFilePath(String path) {
        return path.replace("/", File.separator);
    }

    public static boolean fileExists(String path) {
        return new File(path).exists();
    }

    public static boolean createFolder(String path) {
        return new File(path).mkdirs();
    }

    public static boolean copyFromResources(String resource, String destination) {
        try (InputStream stream = Grasscutter.class.getResourceAsStream(resource)) {
            if (stream == null) {
                Grasscutter.getLogger().warn("Could not find resource: " + resource);
                return false;
            }

            Files.copy(stream, new File(destination).toPath(), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (Exception exception) {
            Grasscutter.getLogger()
                    .warn("Unable to copy resource " + resource + " to " + destination, exception);
            return false;
        }
    }

    public static void logObject(Object object) {
        Grasscutter.getLogger().info(JsonUtils.encode(object));
    }

    public static void startupCheck() {
        ConfigContainer config = Grasscutter.getConfig();
        Logger logger = Grasscutter.getLogger();
        boolean exit = false, custom = false;

        String dataFolder = config.folderStructure.data;

        if (!Files.exists(getResourcePath(""))) {
            logger.info(translate("messages.status.create_resources"));
            logger.info(translate("messages.status.resources_error"));
            createFolder(config.folderStructure.resources);
            exit = true;
        }

        if (!Files.exists(getResourcePath("BinOutput"))
                || !Files.exists(getResourcePath("ExcelBinOutput"))) {
            logger.info(translate("messages.status.resources_error"));
            exit = true;
        }

        if (!fileExists(dataFolder)) createFolder(dataFolder);

        if (!Files.exists(getResourcePath("Server"))) {
            logger.info(translate("messages.status.resources.missing_server"));
            custom = true;
        }

        if (!Files.exists(getResourcePath("ScriptSceneData"))) {
            logger.info(translate("messages.status.resources.missing_scenes"));
            custom = true;
        }

        if (custom) logger.info(translate("messages.status.resources.custom"));

        if (exit) System.exit(1);

        DataLoader.checkAllFiles();
    }

    public static int getNextTimestampOfThisHour(int hour, String timeZone, int param) {
        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of(timeZone));
        for (int i = 0; i < param; i++) {
            if (zonedDateTime.getHour() < hour) {
                zonedDateTime = zonedDateTime.withHour(hour).withMinute(0).withSecond(0);
            } else {
                zonedDateTime = zonedDateTime.plusDays(1).withHour(hour).withMinute(0).withSecond(0);
            }
        }
        return (int) zonedDateTime.toInstant().atZone(ZoneOffset.UTC).toEpochSecond();
    }

    public static int getNextTimestampOfThisHourInNextWeek(int hour, String timeZone, int param) {
        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of(timeZone));
        for (int i = 0; i < param; i++) {
            if (zonedDateTime.getDayOfWeek() == DayOfWeek.MONDAY && zonedDateTime.getHour() < hour) {
                zonedDateTime =
                        ZonedDateTime.now(ZoneId.of(timeZone)).withHour(hour).withMinute(0).withSecond(0);
            } else {
                zonedDateTime =
                        zonedDateTime
                                .with(TemporalAdjusters.next(DayOfWeek.MONDAY))
                                .withHour(hour)
                                .withMinute(0)
                                .withSecond(0);
            }
        }
        return (int) zonedDateTime.toInstant().atZone(ZoneOffset.UTC).toEpochSecond();
    }

    public static int getNextTimestampOfThisHourInNextMonth(int hour, String timeZone, int param) {
        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of(timeZone));
        for (int i = 0; i < param; i++) {
            if (zonedDateTime.getDayOfMonth() == 1 && zonedDateTime.getHour() < hour) {
                zonedDateTime =
                        ZonedDateTime.now(ZoneId.of(timeZone)).withHour(hour).withMinute(0).withSecond(0);
            } else {
                zonedDateTime =
                        zonedDateTime
                                .with(TemporalAdjusters.firstDayOfNextMonth())
                                .withHour(hour)
                                .withMinute(0)
                                .withSecond(0);
            }
        }
        return (int) zonedDateTime.toInstant().atZone(ZoneOffset.UTC).toEpochSecond();
    }

    public static String readFromInputStream(@Nullable InputStream stream) {
        if (stream == null) return "empty";

        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader =
                new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            stream.close();
        } catch (IOException e) {
            Grasscutter.getLogger().warn("Failed to read from input stream.");
        } catch (NullPointerException ignored) {
            return "empty";
        }
        return stringBuilder.toString();
    }

    public static int lerp(int x, int[][] xyArray) {
        try {
            if (x <= xyArray[0][0]) {
                return xyArray[0][1];
            } else if (x >= xyArray[xyArray.length - 1][0]) {
                return xyArray[xyArray.length - 1][1];
            }

            for (int i = 0; i < xyArray.length - 1; i++) {
                if (x == xyArray[i + 1][0]) {
                    return xyArray[i + 1][1];
                }
                if (x < xyArray[i + 1][0]) {

                    int position = x - xyArray[i][0];
                    int fullDist = xyArray[i + 1][0] - xyArray[i][0];
                    int prevValue = xyArray[i][1];
                    int fullDelta = xyArray[i + 1][1] - prevValue;
                    return prevValue + ((position * fullDelta) / fullDist);
                }
            }
        } catch (IndexOutOfBoundsException e) {
            Grasscutter.getLogger()
                    .error("Malformed lerp point array. Must be of form [[x0, y0], ..., [xN, yN]].");
        }
        return 0;
    }

    public static boolean intInArray(int key, int[] array) {
        for (int i : array) {
            if (i == key) {
                return true;
            }
        }
        return false;
    }

    public static int[] setSubtract(int[] minuend, int[] subtrahend) {
        IntList temp = new IntArrayList();
        for (int i : minuend) {
            if (!intInArray(i, subtrahend)) {
                temp.add(i);
            }
        }
        return temp.toIntArray();
    }

    public static String getLanguageCode(Locale locale) {
        return String.format("%s-%s", locale.getLanguage(), locale.getCountry());
    }

    public static String base64Encode(byte[] toEncode) {
        return Base64.getEncoder().encodeToString(toEncode);
    }

    public static byte[] base64Decode(String toDecode) {
        return Base64.getDecoder().decode(toDecode);
    }

    public static <T> T drawRandomListElement(List<T> list, List<Integer> probabilities) {

        if (probabilities == null || probabilities.size() <= 1 || probabilities.size() != list.size()) {
            int index = ThreadLocalRandom.current().nextInt(0, list.size());
            return list.get(index);
        }

        int totalProbabilityMass = probabilities.stream().reduce(Integer::sum).get();
        int roll = ThreadLocalRandom.current().nextInt(1, totalProbabilityMass + 1);

        int currentTotalChance = 0;
        for (int i = 0; i < list.size(); i++) {
            currentTotalChance += probabilities.get(i);

            if (roll <= currentTotalChance) {
                return list.get(i);
            }
        }

        return list.get(0);
    }

    public static <T> T drawRandomListElement(List<T> list) {
        return drawRandomListElement(list, null);
    }

    public static List<String> nonRegexSplit(String input, int separator) {
        var output = new ArrayList<String>();
        int start = 0;
        for (int next = input.indexOf(separator); next > 0; next = input.indexOf(separator, start)) {
            output.add(input.substring(start, next));
            start = next + 1;
        }
        if (start < input.length()) output.add(input.substring(start));
        return output;
    }

    public static String address(Context ctx) {

        var address = ctx.header("CF-Connecting-IP");
        if (address != null) return address;

        address = ctx.header("X-Forwarded-For");
        if (address != null) return address;

        address = ctx.header("X-Real-IP");
        if (address != null) return address;

        return ctx.ip();
    }

    @SuppressWarnings("BusyWait")
    public static void waitFor(Returnable<Boolean> runnable) {
        while (!runnable.invoke()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static List<Field> getAllFields(Class<?> type) {
        var fields = new LinkedList<>(Arrays.asList(type.getDeclaredFields()));

        if (type.getSuperclass() != null) {
            fields.addAll(getAllFields(type.getSuperclass()));
        }

        return fields;
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
        }
    }

    public static String unescapeJson(String json) {
        return json.replaceAll("\"", "\"");
    }
}
