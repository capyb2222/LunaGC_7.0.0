package emu.grasscutter.game.inventory;

import emu.grasscutter.data.excels.reliquary.ReliquaryAffixData;

@FunctionalInterface
public interface ArtifactRollBias {
    ArtifactRollBias NONE = affix -> 1;

    double weigh(ReliquaryAffixData affix);
}
