package emu.grasscutter.data.binout;

import emu.grasscutter.data.GameData;
import java.util.*;

public final class ExtraTalents {

    private static final Set<String> ENABLED = Set.of("Mizuki_ExtraProundSkill");

    private static Map<String, List<String>> byOwner;

    private ExtraTalents() {}

    public static String owner(String openConfig) {
        if (openConfig == null || openConfig.isEmpty()) return "";

        int first = openConfig.indexOf('_');
        if (first < 0) return openConfig;

        String head = openConfig.substring(0, first);
        if (head.equals("Player") || head.equals("PlayerBoy") || head.equals("PlayerGirl")) {
            int second = openConfig.indexOf('_', first + 1);
            String element =
                    second < 0 ? openConfig.substring(first + 1) : openConfig.substring(first + 1, second);
            return "Player_" + element;
        }
        return head;
    }

    public static List<String> forOwners(Collection<String> owners, String avatarName) {
        if (owners == null || owners.isEmpty()) return List.of();
        if (byOwner == null) build();

        var out = new ArrayList<String>();
        for (var ownerKey : owners) {
            for (var openConfig : byOwner.getOrDefault(ownerKey, List.of())) {
                if (isForOtherTravelerBody(openConfig, avatarName)) continue;
                out.add(openConfig);
            }
        }
        return out;
    }

    private static boolean isForOtherTravelerBody(String openConfig, String avatarName) {
        if (!openConfig.startsWith("PlayerBoy_") && !openConfig.startsWith("PlayerGirl_")) return false;
        return avatarName == null || !openConfig.startsWith(avatarName + "_");
    }

    private static synchronized void build() {
        if (byOwner != null) return;

        var referenced = new HashSet<String>();
        for (var proudSkill : GameData.getProudSkillDataMap().values()) {
            var openConfig = proudSkill.getOpenConfig();
            if (openConfig != null && !openConfig.isEmpty()) referenced.add(openConfig);
        }
        for (var talent : GameData.getAvatarTalentDataMap().values()) {
            var openConfig = talent.getOpenConfig();
            if (openConfig != null && !openConfig.isEmpty()) referenced.add(openConfig);
        }

        var map = new HashMap<String, List<String>>();
        for (var entry : GameData.getOpenConfigEntries().values()) {
            var name = entry.getName();
            if (name == null || name.isEmpty() || referenced.contains(name)) continue;
            if (!ENABLED.contains(name)) continue;
            if (!grantsAvatarAbility(entry)) continue;

            map.computeIfAbsent(owner(name), k -> new ArrayList<>()).add(name);
        }

        byOwner = map;
    }

    private static boolean grantsAvatarAbility(OpenConfigEntry entry) {
        var abilities = entry.getAddAbilities();
        if (abilities == null) return false;

        for (var ability : abilities) {
            if (ability != null && ability.startsWith("Avatar_")) return true;
        }
        return false;
    }
}
