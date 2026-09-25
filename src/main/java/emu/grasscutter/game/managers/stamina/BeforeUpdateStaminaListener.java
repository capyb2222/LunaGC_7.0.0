package emu.grasscutter.game.managers.stamina;

public interface BeforeUpdateStaminaListener {
    int onBeforeUpdateStamina(String reason, int newStamina, boolean isCharacterStamina);

    Consumption onBeforeUpdateStamina(
            String reason, Consumption consumption, boolean isCharacterStamina);
}
