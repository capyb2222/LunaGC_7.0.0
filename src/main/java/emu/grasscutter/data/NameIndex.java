package emu.grasscutter.data;

import emu.grasscutter.game.inventory.EquipType;
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

    /** Artifact set names, which name a whole family rather than one piece. */
    private static final Index SETS = new Index();

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

    /** What a slot is called, in all the ways it gets called. */
    private static final Map<String, EquipType> SLOTS =
            Map.ofEntries(
                    Map.entry("flower", EquipType.EQUIP_BRACER),
                    Map.entry("bracer", EquipType.EQUIP_BRACER),
                    Map.entry("plume", EquipType.EQUIP_NECKLACE),
                    Map.entry("feather", EquipType.EQUIP_NECKLACE),
                    Map.entry("necklace", EquipType.EQUIP_NECKLACE),
                    Map.entry("sands", EquipType.EQUIP_SHOES),
                    Map.entry("sand", EquipType.EQUIP_SHOES),
                    Map.entry("timepiece", EquipType.EQUIP_SHOES),
                    Map.entry("hourglass", EquipType.EQUIP_SHOES),
                    Map.entry("goblet", EquipType.EQUIP_RING),
                    Map.entry("cup", EquipType.EQUIP_RING),
                    Map.entry("circlet", EquipType.EQUIP_DRESS),
                    Map.entry("crown", EquipType.EQUIP_DRESS),
                    Map.entry("hat", EquipType.EQUIP_DRESS));

    /**
     * Resolves an artifact named by its set and slot - "gladiator's finale circlet" - to the piece
     * itself.
     *
     * <p>Piece names work on their own, but nobody remembers that the Gladiator circlet is called
     * Gladiator's Triumphal Sign; the set is what is on the wiki and in the head. Nothing is consumed
     * unless a slot really follows a set, so a name that merely starts like one is left alone.
     *
     * @return the piece's id, or 0 if this is not a set followed by a slot
     */
    public static int resolveRelic(String first, List<String> rest) {
        build();

        var match = SETS.match(first, rest);
        if (match == null || match.consumed >= rest.size()) return 0;

        var slot = SLOTS.get(normalise(rest.get(match.consumed)));
        if (slot == null) return 0;

        var piece = bestPiece(match.id, slot);
        if (piece == 0) return 0;

        for (var i = 0; i <= match.consumed; i++) rest.remove(0);
        return piece;
    }

    /**
     * How to write an id in a message people read: "Gladiator's Nostalgia (23414)".
     *
     * <p>Falls back to the bare id for anything unnamed, so a message never loses information by
     * asking for this.
     */
    public static String describe(int id) {
        build();

        var name = nameOf(id);
        return name == null || name.isBlank() ? String.valueOf(id) : name + " (" + id + ")";
    }

    private static String nameOf(int id) {
        var item = GameData.getItemDataMap().get(id);
        if (item != null) return text(item.getNameTextMapHash());

        var avatar = GameData.getAvatarDataMap().get(id);
        if (avatar != null) return text(avatar.getNameTextMapHash());

        var monster = GameData.getMonsterDataMap().get(id);
        if (monster != null) {
            var describe = GameData.getMonsterDescribeDataMap().get(monster.getDescribeId());
            var named = describe == null ? null : text(describe.getNameTextMapHash());
            return named != null ? named : monster.getMonsterName();
        }

        var gadget = GameData.getGadgetDataMap().get(id);
        return gadget == null ? null : gadget.getJsonName();
    }

    /**
     * The piece of a set that fills one slot: the highest rarity it was made in, and among those the
     * lowest id.
     *
     * <p>A slot has far more rows than pieces - Blizzard Strayer's plume alone has nineteen, five of
     * them five star, the rest pre-rolled variants sitting in a much higher id range. The lowest is
     * the one the game itself drops.
     */
    private static int bestPiece(int setId, EquipType slot) {
        var best = 0;
        var bestRank = -1;

        for (var item : GameData.getItemDataMap().values()) {
            if (item.getSetId() != setId || item.getEquipType() != slot) continue;

            if (item.getRankLevel() > bestRank || (item.getRankLevel() == bestRank && item.getId() < best)) {
                bestRank = item.getRankLevel();
                best = item.getId();
            }
        }

        return best;
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

        // A set is named by the bonus it grants rather than by any row of its own, and it points at
        // the affix GROUP - while the affix map is keyed by the individual affixId, one per piece
        // count. Index the group here so the set can find its way to a name.
        var affixNames = new HashMap<Integer, String>();
        GameData.getEquipAffixDataMap()
                .forEach(
                        (affixId, affix) ->
                                affixNames.putIfAbsent(affix.getMainId(), text(affix.getNameTextMapHash())));

        GameData.getReliquarySetDataMap()
                .forEach((setId, set) -> SETS.claim(affixNames.get(set.getEquipAffixId()), setId, 1));
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

        /** Where a phrase matched and how many following words it took, or null for no match. */
        Match match(String first, List<String> rest) {
            var phrase = new StringBuilder(normalise(first));
            var found = this.byName.get(phrase.toString());
            var best = found == null ? null : new Match(found, 0);

            for (var i = 0; i < rest.size(); i++) {
                phrase.append(normalise(rest.get(i)));

                var candidate = this.byName.get(phrase.toString());
                if (candidate != null) best = new Match(candidate, i + 1);
                else if (!isPrefix(phrase.toString())) break;
            }

            return best;
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

    /** A name that matched, and how many words after the first it needed. */
    private record Match(int id, int consumed) {}

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
