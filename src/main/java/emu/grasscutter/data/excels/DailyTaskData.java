package emu.grasscutter.data.excels;

import emu.grasscutter.data.GameResource;
import emu.grasscutter.data.ResourceType;
import java.util.List;
import lombok.Getter;

@Getter
@ResourceType(name = "DailyTaskExcelConfigData.json")
public class DailyTaskData extends GameResource {
    private int id;
    private int cityId;
    private int poolId;
    private String type;
    private List<Integer> newGroupVec;
    private String finishType;
    private int finishProgress;
    private int taskRewardId;

    @Override
    public int getId() {
        return this.id;
    }
}