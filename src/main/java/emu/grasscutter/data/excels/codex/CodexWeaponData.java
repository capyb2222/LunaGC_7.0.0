package emu.grasscutter.data.excels.codex;

import com.google.gson.annotations.SerializedName;
import emu.grasscutter.data.*;

@ResourceType(name = {"WeaponCodexExcelConfigData.json"})
public class CodexWeaponData extends GameResource {
    @SerializedName(value = "id", alternate = "Id")
    private int Id;
    private int weaponId;
    private int gadgetId;
    private int sortOrder;

    public int getSortOrder() {
        return sortOrder;
    }

    public int getWeaponId() {
        return weaponId;
    }

    public int getGadgetId() {
        return weaponId;
    }

    public int getId() {
        return Id;
    }

    @Override
    public void onLoad() {
        GameData.getCodexWeaponDataIdMap().put(this.getWeaponId(), this);
    }
}
