package emu.grasscutter.game.dailytask;

import dev.morphia.annotations.Entity;
import emu.grasscutter.data.GameData;
import emu.grasscutter.game.player.Player;
import javax.annotation.Nullable;
import lombok.Getter;

@Getter
@Entity
public class DailyTask {
    private int rewardId;
    private int taskId;
    private int finishProgress;
    private int progress;
    private boolean finished;

    public DailyTask() {}

    private DailyTask(
            int rewardId,
            int taskId,
            int finishProgress,
            int progress,
            boolean finished) {
        this.rewardId = rewardId;
        this.taskId = taskId;
        this.finishProgress = finishProgress;
        this.progress = progress;
        this.finished = finished;
    }

    @Nullable
    public static DailyTask create(Player owner, int dailyTaskId) {
        var data = GameData.getDailyTaskDataMap().get(dailyTaskId);

        if (data == null || owner.getDailyTaskManager() == null) {
            return null;
        }

        int rewardId =
                owner.getDailyTaskManager()
                        .getRewardId(data.getTaskRewardId());

        return new DailyTask(
                rewardId,
                dailyTaskId,
                data.getFinishProgress(),
                0,
                false);
    }

    public boolean addProgress(int amount) {
        if (this.finished || amount <= 0) {
            return false;
        }

        int oldProgress = this.progress;

        this.progress =
                Math.min(
                        this.finishProgress,
                        this.progress + amount);

        if (this.progress >= this.finishProgress) {
            this.finished = true;
        }

        return this.progress != oldProgress;
    }

    public boolean finish() {
        if (this.finished) {
            return false;
        }

        this.progress = this.finishProgress;
        this.finished = true;
        return true;
    }

    /** This tree has no generated class for the message, so it is written by hand. */
    public byte[] toProto() {
        return DailyTaskProto.info(this);
    }

    /** Named as the message names it, for {@link DailyTaskProto}. */
    public int getDailyTaskId() {
        return this.taskId;
    }
}