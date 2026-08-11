package emu.grasscutter.data.excels;

import emu.grasscutter.data.*;
import emu.grasscutter.data.excels.world.WorldAreaData;
import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;

@ResourceType(name = "CityConfigData.json", loadPriority = ResourceType.LoadPriority.HIGH)
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CityData extends GameResource {
    int cityId;
    int sceneId;
    List<Integer> areaIdVec;

    @Override
    public int getId() {
        return this.cityId;
    }

    /**
     * Which scene this city stands in.
     *
     * <p>The table stopped carrying a scene of its own, so every city answered zero and anything
     * keyed off it went looking under scene 0 - which is not a scene at all. The boss tracker did
     * exactly that, asking for all 88 of its groups from "Scene/0" on every load. A city's areas do
     * still name their scene, and all of a city's areas agree on it, so take it from there.
     *
     * <p>Worked out on first use rather than at load: cities load ahead of world areas, so there is
     * nothing to read yet while this row is being built.
     */
    public int getSceneId() {
        if (this.sceneId != 0 || this.areaIdVec == null) return this.sceneId;

        for (var areaId : this.areaIdVec) {
            // Areas are keyed child first, and a city names top level areas, whose child id is zero.
            WorldAreaData area = GameData.getWorldAreaDataMap().get(areaId.intValue());
            if (area != null && area.getSceneId() != 0) {
                this.sceneId = area.getSceneId();
                break;
            }
        }

        return this.sceneId;
    }
}
