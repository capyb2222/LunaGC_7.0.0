package emu.grasscutter.server.scheduler;

import javax.annotation.Nullable;
import lombok.Getter;

/** A server task that should be run asynchronously. */
public final class AsyncServerTask implements Runnable {
    /* The runnable to run. */
    private final Runnable task;
    /* This ID is assigned by the scheduler. */
    @Getter private final int taskId;
    /* The result callback to run. */
    @Nullable private final Runnable callback;

    /* Has the task already been started? */
    private boolean started = false;
    /* Has the task finished execution? */
    private boolean finished = false;
    /* The result produced in the async task. */
    @Nullable private Object result = null;

    public AsyncServerTask(Runnable task, int taskId) {
        this(task, null, taskId);
    }

    public AsyncServerTask(Runnable task, @Nullable Runnable callback, int taskId) {
        this.task = task;
        this.callback = callback;
        this.taskId = taskId;
    }

    public boolean hasStarted() {
        return this.started;
    }

    public boolean isFinished() {
        return this.finished;
    }

    /** Runs the task. */
    @Override
    public void run() {
        // Declare the task as started.
        this.started = true;

        // Run the runnable.
        this.task.run();

        // Declare the task as finished.
        this.finished = true;
    }

    /** Runs the callback. */
    public void complete() {
        // Run the callback.
        if (this.callback != null) this.callback.run();
    }

    @Nullable public Object getResult() {
        return this.result;
    }

    public void setResult(@Nullable Object result) {
        this.result = result;
    }
}
