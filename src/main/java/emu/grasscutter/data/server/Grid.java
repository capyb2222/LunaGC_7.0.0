package emu.grasscutter.data.server;

import com.github.davidmoten.rtreemulti.RTree;
import com.github.davidmoten.rtreemulti.geometry.Geometry;
import emu.grasscutter.Grasscutter;
import emu.grasscutter.game.world.*;
import emu.grasscutter.scripts.SceneIndexManager;
import java.util.*;

public class Grid {
    /** Hard cap for loading scene entities around a player, in metres. */
    private static final int MAX_ENTITY_LOAD_RANGE = 500;

    public transient RTree<Map.Entry<GridPosition, Set<Integer>>, Geometry> gridOptimized = null;
    private transient Set<Integer> nearbyGroups = new HashSet<>(100);

    public Map<GridPosition, Set<Integer>> grid = new LinkedHashMap<>();

    /** Creates an optimized cache of the grid. */
    private void optimize() {
        if (this.gridOptimized == null) {
            var gridValues = new ArrayList<Map.Entry<GridPosition, Set<Integer>>>();
            this.grid.forEach((k, v) -> gridValues.add(new AbstractMap.SimpleEntry<>(k, v)));
            this.gridOptimized =
                    SceneIndexManager.buildIndex(2, gridValues, entry -> entry.getKey().toPoint());
        }
    }

    /**
     * @return The correctly loaded grid map.
     */
    public Map<GridPosition, Set<Integer>> getGrid() {
        return this.grid;
    }

    public Set<Integer> getNearbyGroups(int vision_level, Position position) {
        this.optimize(); // Check to see if the grid is optimized.

        int width = Grasscutter.getConfig().server.game.visionOptions[vision_level].gridWidth;
        int vision_range = Grasscutter.getConfig().server.game.visionOptions[vision_level].visionRange;

        this.nearbyGroups.clear();

        // When entity-error prevention is disabled, use the upstream behavior exactly:
        // no 200m hard cap, load whatever the configured vision range says.
        if (!Grasscutter.getConfig().server.game.gameOptions.isPreventEntityError) {
            int vision_range_grid = vision_range / width;
            GridPosition pos = new GridPosition(position, width);
            SceneIndexManager.queryNeighbors(gridOptimized, pos.toDoubleArray(), vision_range_grid + 1)
                    .forEach(e -> nearbyGroups.addAll(e.getValue()));
            return this.nearbyGroups;
        }

        // Coarse grids with cells larger than the hard cap cannot guarantee that the
        // entities they return are within 500m of the player. Skip them entirely so we
        // never load distant REMOTE/SUPER groups.
        if (width > MAX_ENTITY_LOAD_RANGE) {
            return this.nearbyGroups;
        }

        // Query range is in grid cells. The R-tree rectangle is [pos-range, pos+range],
        // so the worst-case world-space reach is (range + 1) * width. Cap it at 500m.
        int maxRangeGrid = Math.max(0, MAX_ENTITY_LOAD_RANGE / width - 1);
        int vision_range_grid = Math.min(vision_range, MAX_ENTITY_LOAD_RANGE) / width;
        int queryRange = Math.min(vision_range_grid + 1, maxRangeGrid);

        GridPosition pos = new GridPosition(position, width);

        // Construct a list of nearby groups.
        SceneIndexManager.queryNeighbors(gridOptimized, pos.toDoubleArray(), queryRange)
                .forEach(e -> nearbyGroups.addAll(e.getValue()));
        return this.nearbyGroups;
    }
}
