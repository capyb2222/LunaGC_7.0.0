package emu.grasscutter.data.excels;

import emu.grasscutter.data.GameResource;
import emu.grasscutter.data.ResourceType;
import java.util.List;
import lombok.Getter;

@Getter
@ResourceType(name = "DailyTaskRewardExcelConfigData.json")
public class DailyTaskRewardData extends GameResource {
    private int ID;
    private List<DropInfo> dropVec;

    @Override
    public int getId() {
        return this.ID;
    }

    @Getter
    public static class DropInfo {
        private int dropId;
        private int previewRewardId;
    }
}