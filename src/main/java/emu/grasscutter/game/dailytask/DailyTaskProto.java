package emu.grasscutter.game.dailytask;

import com.google.protobuf.CodedOutputStream;
import emu.grasscutter.Grasscutter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * The wire shape of the daily task messages in 6.7.
 *
 * <p>None of these messages exist in this tree's generated protos, and the ones in the 6.6 fork they
 * were ported from carry 6.6 field numbers, so they are written by hand here instead.
 *
 * <p><b>Where the numbers come from.</b> The 6.7 dump is fully obfuscated, so the messages were
 * identified by shape: {@code DailyTaskInfo} is the only message in all 9402 that is both carried
 * repeatedly by a three-field notify and alone by a one-field notify, while itself holding one bool
 * and four uint32. That triple constraint has exactly one solution, which pins the two notifies and
 * their CmdIds. Within a message a field of a unique type is equally certain - the bool below is
 * one - but a run of same-typed fields is not, and those are marked. If the commission list shows
 * the wrong numbers in game, the marked ones are what to permute.
 */
public final class DailyTaskProto {
    private DailyTaskProto() {}

    /** {@code MCMPKFGDOEM}, the only one-field carrier of the info message. */
    public static final int PROGRESS_NOTIFY_CMD = 24983;

    /** {@code BMPPNHACCGI}, the only three-field carrier. */
    public static final int WORLD_OWNER_NOTIFY_CMD = 28030;

    /**
     * Two messages in the dump have the right shape for this one ({@code AGELMICOGOL} 24670 and
     * {@code JHBIKPALCPF} 29075) and nothing offline separates them. This is the other candidate's
     * twin, so if the commission count never appears, try 29075 with the numbers in the comment on
     * {@link #dataNotify}.
     */
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
