package emu.grasscutter.game.activity.condition;

import emu.grasscutter.data.excels.activity.ActivityCondExcelConfigData;
import emu.grasscutter.game.activity.*;

public abstract class ActivityConditionBaseHandler {

    public abstract boolean execute(
            PlayerActivityData activityData, ActivityConfigItem activityConfig, int... params);
}
