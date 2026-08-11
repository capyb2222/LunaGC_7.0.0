package io.grasscutter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import emu.grasscutter.game.props.FightProperty;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/** The spellings a relic stat arrives in when someone types it at /give. */
public final class StatNameTest {
    @ParameterizedTest(name = "{0} is {1}")
    @CsvSource({
        "cr, FIGHT_PROP_CRITICAL",
        "CR, FIGHT_PROP_CRITICAL",
        "critrate, FIGHT_PROP_CRITICAL",
        "cd, FIGHT_PROP_CRITICAL_HURT",
        "critdmg, FIGHT_PROP_CRITICAL_HURT",
        "CRIT_DMG, FIGHT_PROP_CRITICAL_HURT",
        "atk%, FIGHT_PROP_ATTACK_PERCENT",
        "atkp, FIGHT_PROP_ATTACK_PERCENT",
        "ATK_PERCENT, FIGHT_PROP_ATTACK_PERCENT",
        "hpp, FIGHT_PROP_HP_PERCENT",
        "em, FIGHT_PROP_ELEMENT_MASTERY",
        "mastery, FIGHT_PROP_ELEMENT_MASTERY",
        "er, FIGHT_PROP_CHARGE_EFFICIENCY",
        "pyro, FIGHT_PROP_FIRE_ADD_HURT",
        "PHYS%, FIGHT_PROP_PHYSICAL_ADD_HURT",
        "attack, FIGHT_PROP_ATTACK",
    })
    public void readsAStatHoweverItWasTyped(String typed, String expected) {
        assertEquals(expected, FightProperty.getPropByShortName(typed).name());
    }

    @Test
    @DisplayName("and answers nothing for a word that is not a stat, so the caller can still complain")
    public void unknownNamesResolveToNone() {
        assertEquals(FightProperty.FIGHT_PROP_NONE, FightProperty.getPropByShortName("nonsense"));
        assertEquals(FightProperty.FIGHT_PROP_NONE, FightProperty.getPropByShortName(""));
        assertEquals(FightProperty.FIGHT_PROP_NONE, FightProperty.getPropByShortName(null));
    }
}
