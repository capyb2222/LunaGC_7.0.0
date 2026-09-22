package emu.grasscutter.data.excels.avatar;

import com.google.gson.annotations.SerializedName;
import emu.grasscutter.data.*;
import emu.grasscutter.data.ResourceType.LoadPriority;
import emu.grasscutter.data.common.PropGrowCurve;
import emu.grasscutter.game.props.*;
import emu.grasscutter.utils.Utils;
import it.unimi.dsi.fastutil.ints.*;
import lombok.Getter;

import java.util.*;

    @ResourceType(name = "VehicleExcelConfigData.json", loadPriority = LoadPriority.LOW)
    public class VehicleData extends GameResource {

        @Getter(onMethod_ = @Override)
        @SerializedName(value = "id", alternate = {"ID"})
        private int id;

        @Getter
        private int featureTagGroupID;

}