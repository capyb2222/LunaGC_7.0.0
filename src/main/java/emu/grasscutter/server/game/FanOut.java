package emu.grasscutter.server.game;

import com.google.gson.reflect.TypeToken;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.UnknownFieldSet;
import emu.grasscutter.Grasscutter;
import emu.grasscutter.net.packet.BasePacket;
import emu.grasscutter.utils.FileUtils;
import emu.grasscutter.utils.JsonUtils;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class FanOut {
    public static final class Target {
        public int cmdId;
        public String symbol;
        public Map<String, Integer> fields = new HashMap<>();
    }

    private static Map<String, List<Target>> table;
    private static final Map<String, Map<Integer, String>> numberNames = new ConcurrentHashMap<>();
    private static final Set<String> announced = ConcurrentHashMap.newKeySet();

    private static synchronized Map<String, List<Target>> table() {
        if (table != null) return table;
        table = Map.of();
        var path = FileUtils.getDataUserPath("proto-fanout.json");
        if (!Files.exists(path)) return table;
        try {
            Map<String, List<Target>> loaded =
                    JsonUtils.decode(
                            Files.readString(path), new TypeToken<Map<String, List<Target>>>() {}.getType());
            if (loaded != null) table = loaded;
            Grasscutter.getLogger().info("[fan-out] {} packets resolved by family", table.size());
        } catch (Exception e) {
            Grasscutter.getLogger().warn("[fan-out] could not read {}: {}", path, e.getMessage());
        }
        return table;
    }

    public static boolean send(GameSession session, BasePacket packet) {
        var simple = packet.getClass().getSimpleName();
        if (!simple.startsWith("Packet")) return false;
        var name = simple.substring("Packet".length());
        var targets = table().get(name);
        if (targets == null || targets.isEmpty() || packet.getData() == null) return false;

        var names = numberNames.computeIfAbsent(name, FanOut::numberNames);
        if (names.isEmpty()) return false;

        UnknownFieldSet source;
        try {
            source = UnknownFieldSet.parseFrom(packet.getData());
        } catch (Exception e) {
            return false;
        }

        if (announced.add(name)) {
            Grasscutter.getLogger()
                    .info("[fan-out] {} goes to {} candidate(s)", name, targets.size());
        }

        for (var target : targets) {
            var out = UnknownFieldSet.newBuilder();
            for (var entry : source.asMap().entrySet()) {
                var field = names.get(entry.getKey());
                var number = field == null ? null : target.fields.get(field);
                if (number != null) out.mergeField(number, entry.getValue());
            }
            var copy =
                    packet.shouldBuildHeader()
                            ? new BasePacket(target.cmdId, true)
                            : new BasePacket(target.cmdId);
            if (!packet.shouldBuildHeader() && packet.getHeader() != null) {
                copy.setHeader(packet.getHeader());
            }
            copy.shouldEncrypt = packet.shouldEncrypt;
            copy.setUseDispatchKey(packet.useDispatchKey());
            copy.setData(out.build().toByteArray());
            session.send(copy);
        }
        return true;
    }

    private static Map<Integer, String> numberNames(String name) {
        var out = new HashMap<Integer, String>();
        try {
            var cls = Class.forName("emu.grasscutter.net.proto." + name + "OuterClass$" + name);
            var descriptor = (Descriptor) cls.getMethod("getDescriptor").invoke(null);
            for (var field : descriptor.getFields()) out.put(field.getNumber(), field.getName());
        } catch (Exception ignored) {
        }
        return out;
    }
}
