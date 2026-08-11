package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.dailytask.DailyTaskProto;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BasePacket;

public class PacketDailyTaskDataNotify extends BasePacket {
    public PacketDailyTaskDataNotify(Player player) {
        super(DailyTaskProto.DATA_NOTIFY_CMD);

        var manager = player.getDailyTaskManager();
        this.setData(
                manager == null
                        ? DailyTaskProto.dataNotify(0, 0, false)
                        : DailyTaskProto.dataNotify(
                                manager.getFinishedCount(),
                                manager.getScoreRewardId(),
                                manager.isScoreRewardTaken()));
    }
}
