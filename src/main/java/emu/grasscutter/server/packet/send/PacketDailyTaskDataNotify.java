package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BasePacket;
import emu.grasscutter.net.packet.PacketOpcodes;
import emu.grasscutter.net.proto.DailyTaskDataNotifyOuterClass.DailyTaskDataNotify;

public class PacketDailyTaskDataNotify extends BasePacket {
    public PacketDailyTaskDataNotify(Player player) {
        super(PacketOpcodes.DailyTaskDataNotify);

        var proto = DailyTaskDataNotify.newBuilder();
        var manager = player.getDailyTaskManager();
        if (manager != null) {
            proto.setFinishedNum(manager.getFinishedCount())
                    .setScoreRewardId(manager.getScoreRewardId())
                    .setIsTakenScoreReward(manager.isScoreRewardTaken());
        }

        this.setData(proto);
    }
}
