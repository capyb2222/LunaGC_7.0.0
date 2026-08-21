package emu.grasscutter.game.activity.watcher;

import emu.grasscutter.game.activity.ActivityWatcher;
import emu.grasscutter.game.activity.ActivityWatcherType;
import emu.grasscutter.game.props.WatcherTriggerType;

/** Fires when a monster tracked by the watcher's param list dies. */
@ActivityWatcherType(WatcherTriggerType.TRIGGER_BATTLE_FOR_MONSTER_DIE_OR)
public class MonsterDieWatcher extends ActivityWatcher {
    @Override
    protected boolean isMeet(String... param) {
        if (param.length < 1) return false;

        var paramList = getActivityWatcherData().getTriggerConfig().getParamList();
        if (paramList.isEmpty()) return false;

        // param[0] is the died monster id; a param entry may be a comma-separated list.
        for (String entry : paramList) {
            for (String monsterId : entry.split(",")) {
                if (monsterId.trim().equals(param[0])) return true;
            }
        }
        return false;
    }
}
