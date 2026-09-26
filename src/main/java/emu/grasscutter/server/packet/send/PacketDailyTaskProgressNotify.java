package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.dailytask.DailyTask;
import emu.grasscutter.net.packet.BasePacket;
import emu.grasscutter.net.packet.PacketOpcodes;
import emu.grasscutter.net.proto.DailyTaskProgressNotifyOuterClass.DailyTaskProgressNotify;

public class PacketDailyTaskProgressNotify extends BasePacket {
    public PacketDailyTaskProgressNotify(DailyTask task) {
        super(PacketOpcodes.DailyTaskProgressNotify);

        this.setData(DailyTaskProgressNotify.newBuilder().setInfo(task.toProto()));
    }
}
