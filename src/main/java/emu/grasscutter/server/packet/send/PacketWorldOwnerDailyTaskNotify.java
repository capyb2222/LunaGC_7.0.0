package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.dailytask.DailyTask;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BasePacket;
import emu.grasscutter.net.packet.PacketOpcodes;
import emu.grasscutter.net.proto.WorldOwnerDailyTaskNotifyOuterClass.WorldOwnerDailyTaskNotify;

public class PacketWorldOwnerDailyTaskNotify extends BasePacket {
    public PacketWorldOwnerDailyTaskNotify(Player player) {
        super(PacketOpcodes.WorldOwnerDailyTaskNotify);

        var proto = WorldOwnerDailyTaskNotify.newBuilder();
        var manager = player.getDailyTaskManager();
        if (manager != null) {
            manager.getDailyTasks().stream().map(DailyTask::toProto).forEach(proto::addTaskList);
            proto.setFilterCityId(manager.getCityId()).setFinishedDailyTaskNum(manager.getFinishedCount());
        }

        this.setData(proto);
    }
}
