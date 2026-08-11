package emu.grasscutter.data.excels.codex;

import com.google.gson.annotations.SerializedName;
import emu.grasscutter.data.*;

@ResourceType(name = {"MaterialCodexExcelConfigData.json"})
public class CodexMaterialData extends GameResource {
    @SerializedName(value = "id", alternate = "Id")
    private int Id;
    private int materialId;
    private int sortOrder;

    public int getSortOrder() {
        return sortOrder;
    }

    public int getMaterialId() {
        return materialId;
    }

    public int getId() {
        return Id;
    }

    @Override
    public void onLoad() {
        GameData.getCodexMaterialDataIdMap().put(this.getMaterialId(), this);
    }
}
