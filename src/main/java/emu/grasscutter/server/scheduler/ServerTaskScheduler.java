package emu.grasscutter.server.scheduler;

import java.util.concurrent.ConcurrentHashMap;

public final class ServerTaskScheduler {
    /* A map to contain all running tasks. */
    private final ConcurrentHashMap<Integer, ServerTask> tasks = new ConcurrentHashMap<>();
    /* A map to contain all async tasks. */
    private final ConcurrentHashMap<Integer, AsyncServerTask> asyncTasks = new ConcurrentHashMap<>();

    /* The ID assigned to the next runnable. */
    private int nextTaskId = 0;

    public void runTasks() {
        // Skip if there are no tasks.
        if (this.tasks.size() == 0) return;

        // Run all tasks.
        for (ServerTask task : this.tasks.values()) {
            // Check if the task should run.
            if (task.shouldRun()) {
                // Run the task.
                task.run();
            }

            // Check if the task should be canceled.
            if (task.shouldCancel()) {
                // Cancel the task.
                this.cancelTask(task.getTaskId());
            }
        }

        // Run all async tasks.
        for (AsyncServerTask task : this.asyncTasks.values()) {
            if (!task.hasStarted()) {
                // Create a thread for the task.
                Thread thread = new Thread(task);
                // Start the thread.
                thread.start();
            } else if (task.isFinished()) {
                // Cancel the task.
                this.asyncTasks.remove(task.getTaskId());
                // Run the task's callback.
                task.complete();
            }
        }
    }

    public ServerTask getTask(int taskId) {
        return this.tasks.get(taskId);
    }

    public AsyncServerTask getAsyncTask(int taskId) {
        return this.asyncTasks.get(taskId);
    }

    public void cancelTask(int taskId) {
        this.tasks.remove(taskId);
    }

    public int scheduleAsyncTask(Runnable runnable) {
        // Get the next task ID.
        var taskId = this.nextTaskId++;
        // Create a new task.
        this.asyncTasks.put(taskId, new AsyncServerTask(runnable, taskId));
        // Return the task ID.
        return taskId;
    }

    public int scheduleTask(Runnable runnable) {
        return this.scheduleDelayedRepeatingTask(runnable, -1, -1);
    }

    public int scheduleDelayedTask(Runnable runnable, int delay) {
        return this.scheduleDelayedRepeatingTask(runnable, -1, delay);
    }

    public int scheduleRepeatingTask(Runnable runnable, int period) {
        return this.scheduleDelayedRepeatingTask(runnable, period, 0);
    }

    public int scheduleDelayedRepeatingTask(Runnable runnable, int period, int delay) {
        // Get the next task ID.
        var taskId = this.nextTaskId++;
        // Create a new task.
        this.tasks.put(taskId, new ServerTask(runnable, taskId, period, delay));
        // Return the task ID.
        return taskId;
    }
}
