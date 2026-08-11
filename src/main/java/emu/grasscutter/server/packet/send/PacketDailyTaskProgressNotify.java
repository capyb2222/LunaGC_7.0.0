package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.dailytask.DailyTask;
import emu.grasscutter.game.dailytask.DailyTaskProto;
import emu.grasscutter.net.packet.BasePacket;

public class PacketDailyTaskProgressNotify extends BasePacket {
    public PacketDailyTaskProgressNotify(DailyTask task) {
        super(DailyTaskProto.PROGRESS_NOTIFY_CMD);

        this.setData(DailyTaskProto.progressNotify(task));
    }
}
