package emu.grasscutter.data.excels;

import com.google.gson.annotations.SerializedName;
import emu.grasscutter.data.GameResource;
import emu.grasscutter.data.ResourceType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@ResourceType(name = "HomeworldModuleExcelConfigData.json")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class HomeWorldModuleData extends GameResource {
    @SerializedName(value = "id", alternate = "Id")
    int Id;
    boolean isFree;
    int worldSceneId;
    int defaultRoomSceneId;
}
