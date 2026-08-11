package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.dailytask.DailyTaskProto;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BasePacket;
import java.util.List;

public class PacketWorldOwnerDailyTaskNotify extends BasePacket {
    public PacketWorldOwnerDailyTaskNotify(Player player) {
        super(DailyTaskProto.WORLD_OWNER_NOTIFY_CMD);

        var manager = player.getDailyTaskManager();
        this.setData(
                DailyTaskProto.worldOwnerNotify(
                        manager == null ? List.of() : List.copyOf(manager.getDailyTasks()),
                        manager == null ? 0 : manager.getCityId(),
                        manager == null ? 0 : manager.getFinishedCount()));
    }
}
