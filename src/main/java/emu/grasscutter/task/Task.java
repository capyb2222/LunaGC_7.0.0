package emu.grasscutter.task;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
public @interface Task {
    String taskName() default "NO_NAME";

    String taskCronExpression() default "0 0 0 0 0 ?";

    String triggerName() default "NO_NAME";

    boolean executeImmediatelyAfterReset() default false;

    boolean executeImmediately() default false;
}
