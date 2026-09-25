package emu.grasscutter.game.activity;

import emu.grasscutter.data.excels.activity.ActivityWatcherData;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class ActivityWatcher {
    int watcherId;
    ActivityWatcherData activityWatcherData;
    ActivityHandler activityHandler;

    protected abstract boolean isMeet(String... param);

    protected int getProgressDelta(String... param) {
        return 1;
    }

    public void trigger(PlayerActivityData playerActivityData, String... param) {
        if (isMeet(param)) {
            playerActivityData.addWatcherProgress(watcherId, getProgressDelta(param));
            playerActivityData.save();
        }
    }
}
