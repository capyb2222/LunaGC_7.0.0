package emu.grasscutter.game.managers.stamina;

public interface AfterUpdateStaminaListener {
    void onAfterUpdateStamina(String reason, int newStamina, boolean isCharacterStamina);
}
