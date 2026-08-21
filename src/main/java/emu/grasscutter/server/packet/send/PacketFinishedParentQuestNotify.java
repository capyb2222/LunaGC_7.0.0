package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.quest.GameMainQuest;
import emu.grasscutter.game.quest.enums.ParentQuestState;
import emu.grasscutter.net.packet.*;
import emu.grasscutter.net.proto.FinishedParentQuestNotifyOuterClass.FinishedParentQuestNotify;

public class PacketFinishedParentQuestNotify extends BasePacket {

    public PacketFinishedParentQuestNotify(Player player) {
        super(PacketOpcodes.FinishedParentQuestNotify, true);

        FinishedParentQuestNotify.Builder proto = FinishedParentQuestNotify.newBuilder();

        // Despite the name this sends unfinished parents too, each one flagged not-finished. With
        // questing off that is what makes an old account replay the opening Paimon talk: it still
        // has fifty-odd chapters saved in PARENT_QUEST_STATE_NONE, so the client is told the
        // prologue is live. A fresh account sends none of this, which is why only old ones broke.
        var questingEnabled = emu.grasscutter.config.Configuration.GAME_OPTIONS.questing.enabled;

        for (GameMainQuest mainQuest : player.getQuestManager().getMainQuests().values()) {
            // Canceled Quests do not appear in this packet
            if (mainQuest.getState() == ParentQuestState.PARENT_QUEST_STATE_CANCELED) continue;
            if (!questingEnabled && mainQuest.getState() != ParentQuestState.PARENT_QUEST_STATE_FINISHED)
                continue;
            proto.addParentQuestList(mainQuest.toProto(false));
        }

        this.setData(proto);
    }
}
