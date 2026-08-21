package emu.grasscutter.game.world;

import emu.grasscutter.data.GameData;
import emu.grasscutter.net.proto._LimitedRegionInfoOuterClass._LimitedRegionInfo;
import java.util.Set;
import java.util.TreeSet;

/**
 * The world areas the player is allowed into.
 *
 * <p>`_LimitedRegionInfo` is NEW IN 7.0 - 6.7 has no such field on PlayerEnterSceneNotify,
 * SceneDataNotify or PlayerWorldSceneInfo. It rides alongside `scene_tag_id_list` and
 * `_map_layer_info`, the other two things that describe what of a scene is available, and it is sent
 * on scene entry. A gating concept introduced in the same version as the region that will not open,
 * carried on exactly the message that would establish a barrier.
 *
 * <p>It is read as an allow-list rather than a deny-list: the server has always sent it empty, and
 * empty is the state in which the region is walled off. If it named the regions to RESTRICT then
 * empty would mean "restrict nothing" and there would be no barrier.
 */
public final class WorldRegions {
    private WorldRegions() {}

    private static Set<Integer> areaIds;

    /**
     * Every world area id.
     *
     * <p>The small parent-area numbers from WorldAreaConfigData, the same space scene points use -
     * NOT WorldAreaData.getId(), which is an internal composite of (areaID2 << 16) + areaID1 and
     * runs past 2^32, so it cannot be a wire value.
     */
    public static synchronized Set<Integer> allAreaIds() {
        if (areaIds != null) return areaIds;

        var ids = new TreeSet<Integer>();
        GameData.getWorldAreaDataMap().values().forEach(area -> ids.add(area.getParentArea()));
        ids.remove(0);
        areaIds = ids;
        return ids;
    }

    /** Every area, as the sub-message the scene notifies carry. */
    public static _LimitedRegionInfo unrestricted() {
        return _LimitedRegionInfo.newBuilder().addAllLimitedRegionList(allAreaIds()).build();
    }
}
