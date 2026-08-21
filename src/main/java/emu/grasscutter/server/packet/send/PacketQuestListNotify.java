package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.quest.enums.QuestState;
import emu.grasscutter.net.packet.*;
import emu.grasscutter.net.proto.QuestListNotifyOuterClass.QuestListNotify;

public class PacketQuestListNotify extends BasePacket {

    public PacketQuestListNotify(Player player) {
        super(PacketOpcodes.QuestListNotify, true);

        QuestListNotify.Builder proto = QuestListNotify.newBuilder();

        // With questing off, an account that played with it ON still has its old sub-quests saved,
        // and sending them back as unfinished is what makes the client replay the opening cutscene
        // - no cutscene setting reaches that, because the client decides it from quest state. New
        // accounts have nothing saved, which is why only old ones were affected.
        var questingEnabled = emu.grasscutter.config.Configuration.GAME_OPTIONS.questing.enabled;

        player
                .getQuestManager()
                .forEachQuest(
                        quest -> {
                            var state = quest.getState();
                            if (state == QuestState.QUEST_STATE_UNSTARTED) return;
                            if (!questingEnabled && state != QuestState.QUEST_STATE_FINISHED) return;
                            proto.addQuestList(quest.toProto());
                        });

        this.setData(proto);
    }
}
