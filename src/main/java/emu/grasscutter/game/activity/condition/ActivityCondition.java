package emu.grasscutter.game.activity.condition;

import emu.grasscutter.data.excels.activity.ActivityCondExcelConfigData;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
public @interface ActivityCondition {
    ActivityConditions value();
}
