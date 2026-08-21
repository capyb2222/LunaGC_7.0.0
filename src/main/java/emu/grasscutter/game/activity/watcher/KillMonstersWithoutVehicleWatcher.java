package emu.grasscutter.game.activity.watcher;

import emu.grasscutter.game.activity.ActivityWatcher;
import emu.grasscutter.game.activity.ActivityWatcherType;
import emu.grasscutter.game.props.WatcherTriggerType;

/** Fires when a monster from the watcher's tracked id list dies. */
@ActivityWatcherType(WatcherTriggerType.TRIGGER_KILL_MONSTERS_WITHOUT_VEHICLE)
public class KillMonstersWithoutVehicleWatcher extends ActivityWatcher {
    @Override
    protected boolean isMeet(String... param) {
        if (param.length < 1) return false;

        var paramList = getActivityWatcherData().getTriggerConfig().getParamList();
        if (paramList.size() < 2) return false;

        // The monster id list lives in paramList[1] (paramList[0] is empty for this trigger type).
        for (String monsterId : paramList.get(1).split(",")) {
            if (monsterId.trim().equals(param[0])) return true;
        }
        return false;
    }
}
