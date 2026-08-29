package emu.grasscutter.game.inventory;

import emu.grasscutter.data.excels.reliquary.ReliquaryAffixData;

/**
 * Multiplier applied to a substat's excel weight while a relic rolls its stats.
 *
 * <p>{@link #NONE} leaves the weights exactly as the game data has them, which is what a dungeon
 * drop wants. The artifact shop supplies one that leans towards crit and damage.
 */
@FunctionalInterface
public interface ArtifactRollBias {
    ArtifactRollBias NONE = affix -> 1;

    double weigh(ReliquaryAffixData affix);
}
