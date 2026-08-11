package io.grasscutter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.JsonParser;
import emu.grasscutter.data.binout.AbilityModifier.AbilityModifierAction;
import emu.grasscutter.utils.JsonUtils;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * How an ability action's value reads.
 *
 * <p>The same field carries a multiplier for the actions that multiply by it and a value for the
 * ones that write it, so it defaults to one - and every one of these cases was wrong at some point
 * because of that. A bare "clear this mark" set it instead, and a written {@code true} was passed to
 * a constructor that reads unknown words as the name of a property to look up, so it came out as 0
 * and all 429 animator bools in the configs were broadcast false.
 */
public final class AbilityValueTest {
    private static AbilityModifierAction parse(String json) {
        return JsonUtils.decode(JsonParser.parseString(json), AbilityModifierAction.class);
    }

    private static float written(String json) {
        return parse(json).writtenValue().get(new Object2FloatOpenHashMap<>(), 0f);
    }

    private static float ratio(String json) {
        return parse(json).ratio.get(new Object2FloatOpenHashMap<>(), 0f);
    }

    @Test
    @DisplayName("an action with no value of its own writes zero")
    public void absentValueWritesZero() {
        assertEquals(0f, written("{\"$type\":\"SetGlobalValue\",\"key\":\"X\"}"));
    }

    @Test
    @DisplayName("but still multiplies by one, which is what the shared default is for")
    public void absentValueStillMultipliesByOne() {
        assertEquals(1f, ratio("{\"$type\":\"HealHP\"}"));
    }

    @Test
    @DisplayName("a value written as one really is one")
    public void explicitOneSurvives() {
        assertEquals(1f, written("{\"$type\":\"SetGlobalValue\",\"key\":\"X\",\"value\":1}"));
    }

    @Test
    @DisplayName("zero and fractions are read as written")
    public void otherNumbersAreReadAsWritten() {
        assertEquals(0f, written("{\"$type\":\"SetGlobalValue\",\"key\":\"X\",\"value\":0}"));
        assertEquals(3.5f, written("{\"$type\":\"SetGlobalValue\",\"key\":\"X\",\"value\":3.5}"));
    }

    @Test
    @DisplayName("a written boolean is one or zero, not the name of a property")
    public void booleansAreOneOrZero() {
        assertEquals(1f, written("{\"$type\":\"SetAnimatorBool\",\"value\":true}"));
        assertEquals(0f, written("{\"$type\":\"SetAnimatorBool\",\"value\":false}"));
    }

    @Test
    @DisplayName("a Randomed block keeps its chance and both its branches")
    public void randomedKeepsItsChance() {
        var action =
                parse(
                        "{\"$type\":\"Randomed\",\"chance\":0.5,\"successActions\":[{\"$type\":\"KillSelf\"}],"
                                + "\"failActions\":[{\"$type\":\"KillSelf\"},{\"$type\":\"KillSelf\"}]}");

        assertEquals(0.5f, action.chance.get(new Object2FloatOpenHashMap<>(), 0f));
        assertEquals(1, action.successActions.length);
        assertEquals(2, action.failActions.length);
    }
}
