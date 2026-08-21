package emu.grasscutter.game.activity.watcher;

import emu.grasscutter.game.activity.ActivityWatcher;
import emu.grasscutter.game.activity.ActivityWatcherType;
import emu.grasscutter.game.props.WatcherTriggerType;

/** Fires when a quest tracked by the watcher's param list is finished. */
@ActivityWatcherType(WatcherTriggerType.TRIGGER_FINISH_QUEST_AND)
public class FinishQuestWatcher extends ActivityWatcher {
    @Override
    protected boolean isMeet(String... param) {
        if (param.length < 1) return false;

        var paramList = getActivityWatcherData().getTriggerConfig().getParamList();
        if (paramList.isEmpty()) return false;

        // param[0] is the finished quest id; a param entry may be a comma-separated list.
        for (String entry : paramList) {
            for (String questId : entry.split(",")) {
                if (questId.trim().equals(param[0])) return true;
            }
        }
        return false;
    }
}
