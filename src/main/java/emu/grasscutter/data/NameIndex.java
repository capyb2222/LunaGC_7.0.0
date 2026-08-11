package emu.grasscutter.data;

import emu.grasscutter.game.inventory.ItemType;
import emu.grasscutter.utils.lang.Language;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * Looks an avatar or item up by the name it is called in game.
 *
 * <p>Built once, on first use, so a server that never types a name never pays for it.
 */
public final class NameIndex {
    /** Normalised name to id, sorted so a partial phrase can be tested as a prefix. */
    private static final TreeMap<String, Integer> byName = new TreeMap<>();

    /** What kind of thing claimed each name, so a better claim can take it over. */
    private static final Map<String, Integer> rank = new TreeMap<>();

    private static final int AVATAR = 3, WEAPON = 2, OTHER = 1;

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
    public static synchronized int resolve(String first, List<String> rest) {
        build();

        var phrase = new StringBuilder(normalise(first));
        var best = byName.getOrDefault(phrase.toString(), 0);
        var consumed = 0;

        for (var i = 0; i < rest.size(); i++) {
            phrase.append(normalise(rest.get(i)));
            var candidate = byName.get(phrase.toString());
            if (candidate != null) {
                best = candidate;
                consumed = i + 1;
            } else if (!isPrefix(phrase.toString())) {
                // Nothing is called this, or starts with it, so the next word belongs to something else.
                break;
            }
        }

        for (var i = 0; i < consumed; i++) rest.remove(0);
        return best;
    }

    private static boolean isPrefix(String phrase) {
        var next = byName.ceilingKey(phrase);
        return next != null && next.startsWith(phrase);
    }

    private static void build() {
        if (built) return;
        built = true;

        GameData.getAvatarDataMap()
                .forEach((id, avatar) -> claim(avatar.getNameTextMapHash(), id, AVATAR));

        GameData.getItemDataMap()
                .forEach(
                        (id, item) ->
                                claim(
                                        item.getNameTextMapHash(),
                                        id,
                                        item.getItemType() == ItemType.ITEM_WEAPON ? WEAPON : OTHER));
    }

    /**
     * A name is shared more often than not - a character, their namecard and their story item all
     * answer to it - so the most likely thing to be asked for wins: the character, then a weapon.
     */
    private static void claim(long hash, int id, int claimant) {
        var strings = Language.getTextMapKey(hash);
        if (strings == null) return;

        var name = normalise(strings.get(0));
        if (name.isEmpty()) return;

        if (claimant > rank.getOrDefault(name, 0)) {
            rank.put(name, claimant);
            byName.put(name, id);
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
