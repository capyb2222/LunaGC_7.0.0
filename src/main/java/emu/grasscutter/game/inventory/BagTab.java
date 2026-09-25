package emu.grasscutter.game.inventory;

import it.unimi.dsi.fastutil.ints.*;
import java.util.*;
import java.util.stream.Stream;
import lombok.*;

@RequiredArgsConstructor
public enum BagTab {
    TAB_NONE(0),
    TAB_WEAPON(1),
    TAB_EQUIP(2),
    TAB_AVATAR(3),
    TAB_FOOD(4),
    TAB_MATERIAL(5),
    TAB_QUEST(6),
    TAB_CONSUME(7),
    TAB_WIDGET(8),
    TAB_HOMEWORLD(9);

    private static final Int2ObjectMap<BagTab> map = new Int2ObjectOpenHashMap<>();
    private static final Map<String, BagTab> stringMap = new HashMap<>();

    static {
        Stream.of(BagTab.values())
                .forEach(
                        entry -> {
                            map.put(entry.getValue(), entry);
                            stringMap.put(entry.name(), entry);
                        });
    }

    @Getter private final int value;

    public static BagTab getTypeByValue(int value) {
        return map.getOrDefault(value, TAB_NONE);
    }

    public static BagTab getTypeByName(String name) {
        return stringMap.getOrDefault(name, TAB_NONE);
    }
}
