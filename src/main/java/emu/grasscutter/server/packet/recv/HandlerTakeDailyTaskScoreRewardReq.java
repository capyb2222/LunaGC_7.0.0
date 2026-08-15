package emu.grasscutter.server.packet.recv;

import emu.grasscutter.game.dailytask.DailyTaskManager;
import emu.grasscutter.net.packet.*;
import emu.grasscutter.net.proto.RetcodeOuterClass.Retcode;
import emu.grasscutter.net.proto._TakeDailyTaskScoreRewardReqOuterClass._TakeDailyTaskScoreRewardReq;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketTakeDailyTaskScoreRewardRsp;
import java.util.List;

/**
 * Claims the bonus for finishing the day's four commissions.
 *
 * <p>DailyTaskManager already grants this on its own the moment the fourth commission is finished,
 * so by the time the player presses the button it has usually been paid. That makes {@code
 * claimScoreReward()} return false - "already taken" - which must NOT be reported as a failure, or
 * the button errors on a reward the player has. What the client is owed here is confirmation and the
 * item list, so success is decided by whether the four commissions are done, not by who paid.
 */
@Opcodes(PacketOpcodes.TakeDailyTaskScoreRewardReq)
public class HandlerTakeDailyTaskScoreRewardReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] header, byte[] payload) throws Exception {
        var req = _TakeDailyTaskScoreRewardReq.parseFrom(payload);
        boolean fromAttendance = req.getIsClaimDailyAttendance();

        var manager = session.getPlayer().getDailyTaskManager();
        if (manager == null) {
            session.send(
                    new PacketTakeDailyTaskScoreRewardRsp(Retcode.RET_FAIL_VALUE, fromAttendance));
            return;
        }

        if (manager.getFinishedCount() < DailyTaskManager.getDailyTaskCount()) {
            session.send(
                    new PacketTakeDailyTaskScoreRewardRsp(
                            Retcode.RET_DAILY_TASK_NOT_FINISH_VALUE, fromAttendance));
            return;
        }

        // No-op when the automatic grant already ran; it still saves and resyncs.
        manager.claimScoreReward();

        List<emu.grasscutter.data.common.ItemParamData> items = manager.getScoreRewardItems();
        session.send(
                new PacketTakeDailyTaskScoreRewardRsp(Retcode.RET_SUCC_VALUE, fromAttendance, items));
    }
}
