package emu.grasscutter.data.excels;

import com.google.gson.annotations.SerializedName;
import emu.grasscutter.data.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@ResourceType(name = "MusicGameBasicConfigData.json")
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MusicGameBasicData extends GameResource {
    @Getter(onMethod_ = @Override)
    int id;

    @SerializedName(value = "musicID", alternate = {"musicId"})
    int musicID;
    int musicLevel;
}
