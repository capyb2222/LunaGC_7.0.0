package emu.grasscutter.data.excels;

import com.google.gson.annotations.SerializedName;
import emu.grasscutter.data.GameResource;
import emu.grasscutter.data.ResourceType;
import java.util.List;
import lombok.Getter;

@Getter
@ResourceType(name = "DailyTaskExcelConfigData.json")
public class DailyTaskData extends GameResource {
    @SerializedName(value = "id", alternate = {"ID"})
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