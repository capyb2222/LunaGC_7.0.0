package emu.grasscutter.game.tower;

import static emu.grasscutter.config.Configuration.GAME_OPTIONS;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.data.GameData;
import emu.grasscutter.data.excels.tower.TowerLevelData;
import emu.grasscutter.game.dungeons.*;
import emu.grasscutter.game.player.*;
import emu.grasscutter.game.props.FightProperty;
import emu.grasscutter.net.proto.PropChangeReasonOuterClass.PropChangeReason;
import emu.grasscutter.server.packet.send.*;
import java.util.*;
import lombok.*;

public class TowerManager extends BasePlayerManager {
    private static final List<DungeonSettleListener> towerDungeonSettleListener =
            List.of(new TowerDungeonSettleListener());

    private int currentPossibleStars = 0;
    @Getter private boolean inProgress;
    @Getter private int currentTimeLimit;

    public TowerManager(Player player) {
        super(player);
    }

    public TowerData getTowerData() {
        return this.getPlayer().getTowerData();
    }

    public int getCurrentFloorId() {
        return this.getTowerData().currentFloorId;
    }

    /** floor number: 1 - 12, or 0 when no floor is selected * */
    public int getCurrentFloorNumber() {
        // EntityMonster.recalcStats reads this while scaling tower monsters, and it runs from the
        // constructor - early enough that no floor need be chosen yet. 0 simply scales nothing.
        var floorData = GameData.getTowerFloorDataMap().get(getCurrentFloorId());
        return floorData != null ? floorData.getFloorIndex() : 0;
    }

    public int getCurrentLevelId() {
        return this.getTowerData().currentLevelId + this.getTowerData().currentLevel;
    }

    /** form 1-3 */
    public int getCurrentLevel() {
        return this.getTowerData().currentLevel + 1;
    }

    public void onTick() {
        var challenge = player.getScene().getChallenge();
        if (!inProgress || challenge == null || !challenge.inProgress()) return;

        // Check star conditions and notify client if any failed.
        int stars = getCurLevelStars();
        while (stars < currentPossibleStars) {
            player
                    .getSession()
                    .send(
                            new PacketTowerLevelStarCondNotify(
                                    getTowerData().currentFloorId, getCurrentLevel(), currentPossibleStars));
            currentPossibleStars--;
        }
    }

    public void onBegin() {
        // onTick() already treats a missing scene challenge as normal; this re-reads it from the
        // scene rather than using the one that triggered the call, so it can be null here too.
        var challenge = player.getScene().getChallenge();
        inProgress = true;
        currentTimeLimit = challenge != null ? challenge.getTimeLimit() : 0;

        // Skills are NOT re-enabled here: the floor's own Lua owns that. It holds them off across
        // the chamber change and switches them back on from the worktop the player starts the half
        // with (SetIsAllowUseSkill(1) in the EVENT_SELECT_OPTION action). Sending it from here
        // races that and turns them on while the script means them off.

        // The abyss hands every character a full burst at the start of a chamber.
        this.fillTeamEnergy();
    }

    /**
     * Fills the burst gauge of everyone on the team, as entering a chamber does in the game.
     *
     * <p>Guarded at every step because this runs inside {@link
     * emu.grasscutter.game.dungeons.challenge.WorldChallenge#start()}: throwing here would stop the
     * challenge starting at all, which costs the whole chamber rather than one burst. A depot can be
     * null, and so can its element - the element-less Traveler is the standing example.
     */
    private void fillTeamEnergy() {
        player
                .getTeamManager()
                .getActiveTeam()
                .forEach(
                        entity -> {
                            var depot = entity.getAvatar().getSkillDepot();
                            if (depot == null) return;

                            // Nightsoul characters spend a separate gauge, and addEnergy would top up
                            // an elemental one they never use.
                            var energySkill = depot.getEnergySkillData();
                            if (energySkill != null && energySkill.getSpecialEnergyMin() > 0) {
                                entity.addSpecialEnergy(
                                        entity.getFightProperty(FightProperty.FIGHT_PROP_MAX_SPECIAL_ENERGY));
                                return;
                            }

                            var element = depot.getElementType();
                            if (element == null) return;

                            float max = entity.getFightProperty(element.getMaxEnergyProp());
                            if (max <= 0) return;

                            entity.addEnergy(
                                    max, PropChangeReason.PropChangeReason_PROP_CHANGE_ABILITY, true);
                        });
    }

    public void onEnd() {
        inProgress = false;
    }

    private static final int LEVELS_PER_FLOOR = 3;
    private static final int STARS_PER_LEVEL = 3;
    private static final int STARS_PER_FLOOR = LEVELS_PER_FLOOR * STARS_PER_LEVEL;

    public Map<Integer, TowerLevelRecord> getRecordMap() {
        Map<Integer, TowerLevelRecord> recordMap = getTowerData().recordMap;
        if (recordMap == null) {
            recordMap = new HashMap<>();
            getTowerData().recordMap = recordMap;
        }
        grantEntranceFloors(recordMap);
        return recordMap;
    }

    /**
     * Hands over entrance floors 1-8 already cleared, so an account starts on the floors that
     * actually rotate.
     *
     * <p>The schedule floors are gated twice: TowerAllDataRsp reports {@code
     * is_finished_entrance_floor} from {@link #canEnterScheduleFloor()}, which wants six stars on
     * the last entrance floor, and each floor's own {@code unlockStarCount} wants six stars on the
     * one before it. Full nine-star records on every entrance floor satisfy both. Turn
     * {@code game.tower.skipEntranceFloors} off to play floors 1-8 for real.
     */
    private void grantEntranceFloors(Map<Integer, TowerLevelRecord> recordMap) {
        if (!GAME_OPTIONS.tower.skipEntranceFloors) return;

        var schedule = player.getServer().getTowerSystem().getCurrentTowerScheduleData();
        if (schedule == null) return;

        var entranceFloors = schedule.getEntranceFloorId();
        if (entranceFloors == null || entranceFloors.isEmpty()) return;

        for (int floorId : entranceFloors) {
            // Levels within a floor are consecutive ids from levelIndex 1, which is the same
            // assumption getCurrentLevelId() makes when it walks the floor.
            int firstLevelId = getFirstLevelId(floorId);
            if (firstLevelId == 0) continue;

            var record = recordMap.computeIfAbsent(floorId, TowerLevelRecord::new);
            if (record.getPassedLevelMap() == null) {
                // A record loaded from a save written before the map existed.
                record.setPassedLevelMap(new HashMap<>());
            }

            // Drop chambers that do not belong to this floor. The old /setprop towerlevel faked the
            // unlock by writing chamber id 0 with six stars, and a save that still carries it would
            // report a chamber that does not exist to the client in passed_level_map.
            record
                    .getPassedLevelMap()
                    .keySet()
                    .removeIf(id -> id < firstLevelId || id >= firstLevelId + LEVELS_PER_FLOOR);

            if (record.getStarCount() >= STARS_PER_FLOOR) continue;

            for (int i = 0; i < LEVELS_PER_FLOOR; i++) {
                record.setLevelStars(firstLevelId + i, STARS_PER_LEVEL);
            }
            record.setFloorStarRewardProgress(STARS_PER_FLOOR);
        }

        // Clearing a floor also opens the next one by giving it an empty record - that is what
        // notifyCurLevelRecordChangeWhenDone does every time. Granting the stars without it leaves
        // the floor after the corridor with no record at all, which is not a state the game can
        // otherwise reach, and the client shows it locked.
        int firstScheduleFloor =
                player
                        .getServer()
                        .getTowerSystem()
                        .getNextFloorId(entranceFloors.get(entranceFloors.size() - 1));
        if (firstScheduleFloor > 0) {
            recordMap.computeIfAbsent(firstScheduleFloor, TowerLevelRecord::new);
        }
    }

    /** Id of a floor's first chamber, or 0 if the resources do not describe the floor. */
    private static int getFirstLevelId(int floorId) {
        var floorData = GameData.getTowerFloorDataMap().get(floorId);
        if (floorData == null) return 0;
        return GameData.getTowerLevelDataMap().values().stream()
                .filter(x -> x.getLevelGroupId() == floorData.getLevelGroupId() && x.getLevelIndex() == 1)
                .findFirst()
                .map(TowerLevelData::getId)
                .orElse(0);
    }

    public void teamSelect(int floor, List<List<Long>> towerTeams) {
        var floorData = GameData.getTowerFloorDataMap().get(floor);
        if (floorData == null) {
            Grasscutter.getLogger().warn("Tower team select for unknown floor {}", floor);
            return;
        }
        getTowerData().currentFloorId = floorData.getFloorId();
        getTowerData().currentLevel = 0;
        getTowerData().currentLevelId = getFirstLevelId(floor);

        if (getTowerData().entryScene == 0) {
            getTowerData().entryScene = player.getSceneId();
        }

        // The teams the client picked are the whole point of this packet, and every way they can go
        // missing looks identical in game - the overworld team just walks in instead. Say what
        // arrived: no teams at all means the request did not carry them, whereas teams that arrive
        // and then get refused are reported by setupTemporaryTeam.
        Grasscutter.getLogger()
                .info(
                        "Tower team select on floor {}: {} team(s), sizes {}",
                        floor,
                        towerTeams.size(),
                        towerTeams.stream().map(List::size).toList());

        player.getTeamManager().setupTemporaryTeam(towerTeams);
    }

    public TowerLevelData getCurrentTowerLevelDataMap() {
        return GameData.getTowerLevelDataMap().get(getCurrentLevelId());
    }

    public int getCurrentMonsterLevel() {
        // monsterLevel given in TowerLevelExcelConfigData.json is off by one.
        var levelData = getCurrentTowerLevelDataMap();
        if (levelData != null) {
            return levelData.getMonsterLevel() + 1;
        }
        // Spawning is not worth aborting over a missing row; the floor's own override is the same
        // number the client shows for the floor.
        var floorData = GameData.getTowerFloorDataMap().get(getCurrentFloorId());
        Grasscutter.getLogger()
                .warn("No tower level data for level {}, falling back to the floor level", getCurrentLevelId());
        return floorData != null ? floorData.getOverrideMonsterLevel() : 1;
    }

    public void enterLevel(int enterPointId) {
        var levelData = getCurrentTowerLevelDataMap();
        if (levelData == null) {
            // No level means no dungeon to hand off to; entering would NPE on the way in.
            Grasscutter.getLogger()
                    .warn(
                            "Tower enter level {} on floor {} has no level data",
                            getCurrentLevelId(),
                            getCurrentFloorId());
            return;
        }

        var dungeonId = levelData.getDungeonId();

        notifyCurLevelRecordChange();
        // use team user choose
        player.getTeamManager().useTemporaryTeam(0);
        player
                .getServer()
                .getDungeonSystem()
                .handoffDungeon(player, dungeonId, towerDungeonSettleListener);

        // make sure user can exit dungeon correctly
        player.getScene().setPrevScene(getTowerData().entryScene);
        player.getScene().setPrevScenePoint(enterPointId);

        player
                .getSession()
                .send(new PacketTowerEnterLevelRsp(getTowerData().currentFloorId, getCurrentLevel()));
        // stop using skill
        player.getSession().send(new PacketCanUseSkillNotify(false));
        // notify the cond of stars
        currentPossibleStars = 3;
        player
                .getSession()
                .send(
                        new PacketTowerLevelStarCondNotify(
                                getTowerData().currentFloorId, getCurrentLevel(), currentPossibleStars + 1));
    }

    public void notifyCurLevelRecordChange() {
        player
                .getSession()
                .send(
                        new PacketTowerCurLevelRecordChangeNotify(
                                getTowerData().currentFloorId, getCurrentLevel()));
    }

    public int getCurLevelStars() {
        var scene = player.getScene();
        var challenge = scene.getChallenge();
        if (challenge == null) {
            Grasscutter.getLogger().error("getCurLevelStars: no challenge registered!");
            return 0;
        }

        var levelData = getCurrentTowerLevelDataMap();
        // 0-based indexing. "star" = 0 means checking for 1-star conditions.
        int star;
        for (star = 2; star >= 0; star--) {
            var cond = levelData.getCondType(star);
            if (cond == TowerLevelData.TowerCondType.TOWER_COND_CHALLENGE_LEFT_TIME_MORE_THAN) {
                var params = levelData.getTimeCond(star);
                var timeRemaining =
                        challenge.getTimeLimit() - (scene.getSceneTimeSeconds() - challenge.getStartedAt());
                if (timeRemaining >= params.getMinimumTimeInSeconds()) {
                    break;
                }
            } else if (cond == TowerLevelData.TowerCondType.TOWER_COND_LEFT_HP_GREATER_THAN) {
                var params = levelData.getHpCond(star);
                var hpPercent = challenge.getGuardEntityHpPercent();
                if (hpPercent >= params.getMinimumHpPercentage()) {
                    break;
                }
            } else {
                Grasscutter.getLogger()
                        .error(
                                "getCurLevelStars: Tower level {} has no or unknown condition defined for {} stars",
                                getCurrentLevelId(),
                                star + 1);
                continue;
            }
        }
        return star + 1;
    }

    public void notifyCurLevelRecordChangeWhenDone(int stars) {
        Map<Integer, TowerLevelRecord> recordMap = this.getRecordMap();
        int currentFloorId = getTowerData().currentFloorId;
        if (!recordMap.containsKey(currentFloorId)) {
            recordMap.put(
                    currentFloorId,
                    new TowerLevelRecord(currentFloorId).setLevelStars(getCurrentLevelId(), stars));
        } else {
            // Only update record if better than previous
            var prevRecord = recordMap.get(currentFloorId);
            var passedLevelMap = prevRecord.getPassedLevelMap();
            int prevStars = 0;
            if (passedLevelMap.containsKey(getCurrentLevelId())) {
                prevStars = prevRecord.getLevelStars(getCurrentLevelId());
            }
            if (stars > prevStars) {
                recordMap.put(currentFloorId, prevRecord.setLevelStars(getCurrentLevelId(), stars));
            }
        }

        this.getTowerData().currentLevel++;

        if (!this.hasNextLevel()) {
            // set up the next floor - but floor 12 is the last one, and getNextFloorId returns 0
            // there. Recording floor 0 would put a bogus entry in the save and then report it back
            // in TowerAllDataRsp as a real floor.
            var nextFloorId = this.getNextFloorId();
            if (nextFloorId > 0) {
                recordMap.computeIfAbsent(nextFloorId, TowerLevelRecord::new);
                player.getSession().send(new PacketTowerCurLevelRecordChangeNotify(nextFloorId, 1));
            }
        } else {
            player
                    .getSession()
                    .send(new PacketTowerCurLevelRecordChangeNotify(currentFloorId, getCurrentLevel()));
        }
    }

    public boolean hasNextLevel() {
        return getTowerData().currentLevel < 3;
    }

    public int getNextFloorId() {
        return this.player
                .getServer()
                .getTowerSystem()
                .getNextFloorId(this.getTowerData().currentFloorId);
    }

    public boolean hasNextFloor() {
        return this.player
                        .getServer()
                        .getTowerSystem()
                        .getNextFloorId(this.getTowerData().currentFloorId)
                > 0;
    }

    public void clearEntry() {
        getTowerData().entryScene = 0;
    }

    public boolean canEnterScheduleFloor() {
        Map<Integer, TowerLevelRecord> recordMap = this.getRecordMap();
        if (!recordMap.containsKey(this.player.getServer().getTowerSystem().getLastEntranceFloor())) {
            return false;
        }
        return recordMap
                        .get(this.player.getServer().getTowerSystem().getLastEntranceFloor())
                        .getStarCount()
                >= 6;
    }

    public void mirrorTeamSetUp(int teamId) {
        // use team user choose
        player.getTeamManager().useTemporaryTeam(teamId);
        player.sendPacket(new PacketTowerMiddleLevelChangeTeamNotify());

        // The second half starts with full bursts. Skills stay off until the player takes the
        // worktop option the floor's Lua puts up right after this call - that is what turns them
        // back on, and what spawns the second half's monsters.
        this.fillTeamEnergy();
    }
}
