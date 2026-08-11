package emu.grasscutter.data;

import emu.grasscutter.game.inventory.ItemType;
import emu.grasscutter.utils.lang.Language;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * Looks an avatar, item, monster or gadget up by the name it goes by.
 *
 * <p>Built once, on first use, so a server nobody types a name at never pays for it.
 */
public final class NameIndex {
    /** What /give accepts: the things a player can be handed. */
    private static final Index THINGS = new Index();

    /** What /spawn accepts: the things that can stand in a scene. */
    private static final Index ENTITIES = new Index();

    private static boolean built;

    private NameIndex() {}

    /**
     * Resolves a name typed where an id was expected, taking as many following words as keep
     * matching - "crystalline sword" is two arguments but one name.
     *
     * @param first the word that failed to parse as a number
     * @param rest the arguments after it; the ones consumed are removed
     * @return the id, or 0 if nothing goes by that name
     */
    public static int resolve(String first, List<String> rest) {
        build();
        return THINGS.resolve(first, rest);
    }

    /** As {@link #resolve}, over the things that can be spawned rather than given. */
    public static int resolveEntity(String first, List<String> rest) {
        build();
        return ENTITIES.resolve(first, rest);
    }

    private static synchronized void build() {
        if (built) return;
        built = true;

        // A name is shared more often than not - a character, their namecard and their story item
        // all answer to it - so the most likely thing to be asked for outranks the rest.
        GameData.getAvatarDataMap()
                .forEach((id, avatar) -> THINGS.claim(text(avatar.getNameTextMapHash()), id, 3));

        GameData.getItemDataMap()
                .forEach(
                        (id, item) -> {
                            var name = text(item.getNameTextMapHash());
                            var weapon = item.getItemType() == ItemType.ITEM_WEAPON;
                            THINGS.claim(name, id, weapon ? 2 : 1);
                            ENTITIES.claim(name, id, 1);
                        });

        GameData.getMonsterDataMap()
                .forEach(
                        (id, monster) -> {
                            var describe = GameData.getMonsterDescribeDataMap().get(monster.getDescribeId());
                            if (describe != null) ENTITIES.claim(text(describe.getNameTextMapHash()), id, 3);

                            // Monsters carry a readable internal name where the excel table's own
                            // name hash resolves to nothing at all, which is every one of them here.
                            ENTITIES.claim(monster.getMonsterName(), id, 2);
                        });

        GameData.getGadgetDataMap()
                .forEach((id, gadget) -> ENTITIES.claim(gadget.getJsonName(), id, 2));
    }

    private static String text(long hash) {
        var strings = Language.getTextMapKey(hash);
        return strings == null ? null : strings.get(0);
    }

    /** One searchable set of names. */
    private static final class Index {
        /** Sorted, so a partial phrase can be tested as a prefix of something longer. */
        private final TreeMap<String, Integer> byName = new TreeMap<>();

        /** What kind of thing claimed each name, so a better claim can take it over. */
        private final Map<String, Integer> rank = new HashMap<>();

        void claim(String name, int id, int claimant) {
            var key = normalise(name);
            if (key.isEmpty()) return;

            // Same name, same standing: keep the lower id, which is the plain form of a thing that
            // ships in level or difficulty variants.
            var held = this.rank.getOrDefault(key, 0);
            if (claimant < held || (claimant == held && this.byName.get(key) <= id)) return;

            this.rank.put(key, claimant);
            this.byName.put(key, id);
        }

        int resolve(String first, List<String> rest) {
            var phrase = new StringBuilder(normalise(first));
            var best = this.byName.getOrDefault(phrase.toString(), 0);
            var consumed = 0;

            for (var i = 0; i < rest.size(); i++) {
                phrase.append(normalise(rest.get(i)));

                var candidate = this.byName.get(phrase.toString());
                if (candidate != null) {
                    best = candidate;
                    consumed = i + 1;
                } else if (!isPrefix(phrase.toString())) {
                    // Nothing is called this or starts with it, so the rest is not part of the name.
                    break;
                }
            }

            for (var i = 0; i < consumed; i++) rest.remove(0);
            return best;
        }

        private boolean isPrefix(String phrase) {
            var next = this.byName.ceilingKey(phrase);
            return next != null && next.startsWith(phrase);
        }
    }

    /** Names are matched on their letters alone, so case, spacing and punctuation do not count. */
    private static String normalise(String text) {
        if (text == null) return "";

        var builder = new StringBuilder(text.length());
        for (var c : text.toLowerCase(Locale.ROOT).toCharArray()) {
            if (Character.isLetterOrDigit(c)) builder.append(c);
        }

        return builder.toString();
    }
}
