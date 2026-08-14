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

    /**
     * Tells the client a main quest is finished without the server holding any data for it.
     *
     * <p>Region gates are quest-driven, and a region released after this server's resource set was
     * cut has no quest data here at all - so {@code /quest finish} cannot reach it. `ParentQuest`
     * only needs an id and the finished flag for the client to believe it, which is enough to test
     * whether a barrier is quest-gated. Nothing is persisted: this lasts until the player relogs.
     */
    public PacketFinishedParentQuestUpdateNotify(int... mainQuestIds) {
        super(PacketOpcodes.FinishedParentQuestUpdateNotify);

        var proto = FinishedParentQuestUpdateNotify.newBuilder();
        for (int id : mainQuestIds) {
            proto.addParentQuestList(
                    emu.grasscutter.net.proto.ParentQuestOuterClass.ParentQuest.newBuilder()
                            .setParentQuestId(id)
                            .setIsFinished(true)
                            .setParentQuestState(3) // PARENT_QUEST_STATE_FINISHED
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
