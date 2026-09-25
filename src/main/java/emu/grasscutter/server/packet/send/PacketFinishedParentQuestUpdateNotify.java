package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.quest.GameMainQuest;
import emu.grasscutter.net.packet.*;
import emu.grasscutter.net.proto.FinishedParentQuestUpdateNotifyOuterClass.FinishedParentQuestUpdateNotify;
import java.util.List;

public class PacketFinishedParentQuestUpdateNotify extends BasePacket {

    public PacketFinishedParentQuestUpdateNotify(GameMainQuest quest) {
        super(PacketOpcodes.FinishedParentQuestUpdateNotify);

        FinishedParentQuestUpdateNotify proto =
                FinishedParentQuestUpdateNotify.newBuilder()
                        .addParentQuestList(quest.toProto(true))
                        .build();

        this.setData(proto);
    }

    public PacketFinishedParentQuestUpdateNotify(int... mainQuestIds) {
        super(PacketOpcodes.FinishedParentQuestUpdateNotify);

        var proto = FinishedParentQuestUpdateNotify.newBuilder();
        for (int id : mainQuestIds) {
            proto.addParentQuestList(
                    emu.grasscutter.net.proto.ParentQuestOuterClass.ParentQuest.newBuilder()
                            .setParentQuestId(id)
                            .setIsFinished(true)
                            .setParentQuestState(
                                    emu.grasscutter.game.quest.enums.ParentQuestState
                                            .PARENT_QUEST_STATE_FINISHED
                                            .getValue())
                            .build());
        }
        this.setData(proto);
    }

    public PacketFinishedParentQuestUpdateNotify(List<GameMainQuest> quests) {
        super(PacketOpcodes.FinishedParentQuestUpdateNotify);

        var proto = FinishedParentQuestUpdateNotify.newBuilder();

        for (GameMainQuest mainQuest : quests) {
            proto.addParentQuestList(mainQuest.toProto(true));
        }
        proto.build();
        this.setData(proto);
    }
}
