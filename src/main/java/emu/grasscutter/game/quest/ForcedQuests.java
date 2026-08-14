package emu.grasscutter.game.quest;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.server.packet.send.PacketFinishedParentQuestUpdateNotify;
import emu.grasscutter.utils.FileUtils;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Marks main quests finished for the CLIENT, with no quest data behind them.
 *
 * <p>The quest system can only finish a quest it has data for, and this server's resource set is
 * older than the client - the newest few hundred main quests, Snezhnaya's region gate among them,
 * are simply absent. `ParentQuest` needs nothing but an id and the finished flag for the client to
 * believe it, so the notify is forged directly.
 *
 * <p>The id list ships inside the jar for the same reason: depending on the resource set would
 * reintroduce exactly the gap this works around.
 */
public final class ForcedQuests {
    private ForcedQuests() {}

    /** Sent in batches - one notify carrying four thousand quests is a needlessly large packet. */
    private static final int BATCH = 512;

    private static List<Integer> allMainQuests;

    /** Every main quest id the client knows about, across both the old and new data sets. */
    public static synchronized List<Integer> allMainQuests() {
        if (allMainQuests != null) return allMainQuests;

        var ids = new ArrayList<Integer>();
        try {
            var raw = FileUtils.readResource("/quests/main_quest_ids.txt");
            for (var line : new String(raw, StandardCharsets.UTF_8).split("\\R")) {
                line = line.trim();
                if (line.isEmpty()) continue;
                try {
                    ids.add(Integer.parseInt(line));
                } catch (NumberFormatException ignored) {
                    // A malformed line costs one quest, not the whole list.
                }
            }
        } catch (Exception e) {
            Grasscutter.getLogger().error("Could not read the bundled main quest id list.", e);
        }
        allMainQuests = ids;
        return ids;
    }

    /** Tells the client these are finished, without touching the player's saved state. */
    public static void notify(Player player, Collection<Integer> questIds) {
        var batch = new ArrayList<Integer>(BATCH);
        for (var id : questIds) {
            batch.add(id);
            if (batch.size() == BATCH) {
                send(player, batch);
                batch.clear();
            }
        }
        if (!batch.isEmpty()) send(player, batch);
    }

    /** Marks them finished, remembers it, and tells the client. Survives a relog. */
    public static int apply(Player player, Collection<Integer> questIds) {
        var forced = player.getForcedFinishedQuests();
        int before = forced.size();
        forced.addAll(questIds);
        player.save();
        notify(player, questIds);
        return forced.size() - before;
    }

    private static void send(Player player, List<Integer> ids) {
        var arr = new int[ids.size()];
        for (int i = 0; i < ids.size(); i++) arr[i] = ids.get(i);
        player.sendPacket(new PacketFinishedParentQuestUpdateNotify(arr));
    }
}
