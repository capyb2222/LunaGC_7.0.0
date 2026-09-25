package emu.grasscutter.game.dailytask;

import com.google.protobuf.CodedOutputStream;
import emu.grasscutter.Grasscutter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public final class DailyTaskProto {
    private DailyTaskProto() {}

    /** {@code MCMPKFGDOEM}, the only one-field carrier of the info message. */
    public static final int PROGRESS_NOTIFY_CMD = 24983;

    /** {@code BMPPNHACCGI}, the only three-field carrier. */
    public static final int WORLD_OWNER_NOTIFY_CMD = 28030;

    public static final int DATA_NOTIFY_CMD = 24670;

    // DailyTaskInfo, obf FILHKPEJJPM. The bool is alone in its type and so is certain; the four
    // uint32 are assigned in declaration order against 6.6's, which is a guess.
    private static final int INFO_IS_FINISHED = 4; // certain
    private static final int INFO_REWARD_ID = 10; // guess
    private static final int INFO_TASK_ID = 7; // guess
    private static final int INFO_FINISH_PROGRESS = 8; // guess
    private static final int INFO_PROGRESS = 2; // guess

    // WorldOwnerDailyTaskNotify, obf BMPPNHACCGI. Types line up one for one with 6.6 here.
    private static final int OWNER_TASK_LIST = 14;
    private static final int OWNER_FILTER_CITY = 9;
    private static final int OWNER_FINISHED_NUM = 2;

    // DailyTaskProgressNotify, obf MCMPKFGDOEM. Its only field, and 6.6 numbered it 3 as well.
    private static final int PROGRESS_INFO = 3;

    // DailyTaskDataNotify, obf AGELMICOGOL. The 29075 candidate would be 3 / 14 / 4 / 8.
    private static final int DATA_TASK_LIST = 4;
    private static final int DATA_FINISHED_NUM = 11;
    private static final int DATA_TAKEN_REWARD = 3;
    private static final int DATA_SCORE_REWARD_ID = 6;

    /** One task, as the client reads it. */
    public static byte[] info(DailyTask task) {
        return write(
                out -> {
                    out.writeBool(INFO_IS_FINISHED, task.isFinished());
                    out.writeUInt32(INFO_REWARD_ID, task.getRewardId());
                    out.writeUInt32(INFO_TASK_ID, task.getDailyTaskId());
                    out.writeUInt32(INFO_FINISH_PROGRESS, task.getFinishProgress());
                    out.writeUInt32(INFO_PROGRESS, task.getProgress());
                });
    }

    /** The whole board: every task, the city they were drawn from, and how many are done. */
    public static byte[] worldOwnerNotify(List<DailyTask> tasks, int cityId, int finished) {
        return write(
                out -> {
                    for (var task : tasks) out.writeByteArray(OWNER_TASK_LIST, info(task));
                    out.writeUInt32(OWNER_FILTER_CITY, cityId);
                    out.writeUInt32(OWNER_FINISHED_NUM, finished);
                });
    }

    /** One task moved. */
    public static byte[] progressNotify(DailyTask task) {
        return write(out -> out.writeByteArray(PROGRESS_INFO, info(task)));
    }

    /** The bonus reward's state, once enough commissions are done. */
    public static byte[] dataNotify(int finished, int scoreRewardId, boolean taken) {
        return write(
                out -> {
                    out.writeUInt32(DATA_FINISHED_NUM, finished);
                    out.writeUInt32(DATA_SCORE_REWARD_ID, scoreRewardId);
                    out.writeBool(DATA_TAKEN_REWARD, taken);
                });
    }

    /** Writes a message body, since a length-delimited field is just its bytes. */
    private static byte[] write(Body body) {
        var bytes = new ByteArrayOutputStream();
        var out = CodedOutputStream.newInstance(bytes);

        try {
            body.writeTo(out);
            out.flush();
        } catch (IOException e) {
            Grasscutter.getLogger().error("Could not write a daily task message.", e);
            return new byte[0];
        }

        return bytes.toByteArray();
    }

    private interface Body {
        void writeTo(CodedOutputStream out) throws IOException;
    }
}
