package emu.grasscutter.data.excels.world;

import com.google.gson.annotations.SerializedName;
import emu.grasscutter.data.*;
import emu.grasscutter.game.props.ElementType;
import lombok.Getter;

/**
 * Note the capitalisation on every name here.
 *
 * <p>The shipped table spells these {@code sceneID}, {@code areaID1} and so on, and Gson matches
 * names exactly - so with only the capitalised spellings declared, every field read zero and {@link
 * #getId()} answered zero for all 524 rows, collapsing the whole map onto a single entry. The old
 * spellings stay as alternates in case a table that uses them turns up.
 */
@ResourceType(name = "WorldAreaConfigData.json")
public class WorldAreaData extends GameResource {
    @SerializedName(value = "id", alternate = "ID")
    private int ID;

    @Getter private ElementType elementType;

    @Getter
    @SerializedName(value = "areaNameTextMapHash", alternate = "AreaNameTextMapHash")
    private long textMapHash;

    @Getter
    @SerializedName(value = "areaID1", alternate = "AreaID1")
    private int parentArea;

    @Getter
    @SerializedName(value = "areaID2", alternate = "AreaID2")
    private int childArea;

    @Getter
    @SerializedName(value = "sceneID", alternate = "SceneID")
    private int sceneId;

    @Override
    public int getId() {
        return (this.childArea << 16) + this.parentArea;
    }
}
