package emu.grasscutter.game.ability.actions;

import emu.grasscutter.data.binout.AbilityModifier.AbilityModifierAction;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
public @interface AbilityAction {
    /** One or more action types this handler answers to. */
    AbilityModifierAction.Type[] value();
}
