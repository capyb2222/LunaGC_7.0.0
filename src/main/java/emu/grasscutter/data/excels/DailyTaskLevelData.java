package emu.grasscutter.data.excels;

import emu.grasscutter.data.GameResource;
import emu.grasscutter.data.ResourceType;
import lombok.Getter;

@Getter
@ResourceType(name = "DailyTaskLevelExcelConfigData.json")
public class DailyTaskLevelData extends GameResource {
    private int ID;
    private int minPlayerLevel;
    private int maxPlayerLevel;
    private int groupReviseLevel;
    private int scoreDropId;
    private int scorePreviewRewardId;

    @Override
    public int getId() {
        return this.ID;
    }
}