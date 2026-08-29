package emu.grasscutter.command.commands;

import static emu.grasscutter.utils.lang.Language.translate;

import emu.grasscutter.command.*;
import emu.grasscutter.data.GameData;
import emu.grasscutter.game.player.*;
import emu.grasscutter.server.packet.send.*;
import java.util.*;
import java.util.stream.IntStream;

@Command(
        label = "unlockall",
        usage = {""},
        permission = "player.unlockall",
        permissionTargeted = "player.unlockall.others")
public final class UnlockAllCommand implements CommandHandler {

    private static final List<Integer> SCENE_AREAS = IntStream.range(1, 1000).boxed().toList();

    @Override
    public void execute(Player sender, Player targetPlayer, List<String> args) {

        for (var state : GameData.getOpenStateList()) {
            if (PlayerProgressManager.BLACKLIST_OPEN_STATES.contains(state.getId())) continue;
            if (targetPlayer.getProgressManager().getOpenState(state.getId()) == 0) {
                targetPlayer.getOpenStates().put(state.getId(), 1);
            }
        }
        // the whole map at once - a change notify pops a window per state, ~690 of them
        targetPlayer.sendPacket(new PacketOpenStateUpdateNotify(targetPlayer));

        GameData.getScenePointsPerScene().forEach((sceneId, scenePoints) -> {
            var points = new ArrayList<Integer>();
            for (var pointId : scenePoints) {
                var entry = GameData.getScenePointEntryById(sceneId, pointId);
                if (entry == null) continue;
                var data = entry.getPointData();
                if (data.isForbidSimpleUnlock()) continue;
                if (data.getType() != null && data.getType().equals("SceneBuildingPoint") && !data.isUnlocked()) continue;
                points.add(pointId);
            }
            targetPlayer.getUnlockedScenePoints(sceneId).addAll(points);
            targetPlayer.getUnlockedSceneAreas(sceneId).addAll(SCENE_AREAS);
        });

        int curScene = targetPlayer.getSceneId();
        targetPlayer.sendPacket(new PacketScenePointUnlockNotify(
                curScene, targetPlayer.getUnlockedScenePoints(curScene)));
        targetPlayer.sendPacket(new PacketSceneAreaUnlockNotify(
                curScene, targetPlayer.getUnlockedSceneAreas(curScene)));

        // straight onto the lists: addFlycloak/addTraceEffect pop a window each, and
        // addTraceEffect saves the whole player every time
        GameData.getAvatarFlycloakDataMap()
                .keySet()
                .forEach(targetPlayer.getFlyCloakList()::add);
        GameData.getAvatarTraceEffectDataMap()
                .keySet()
                .forEach(targetPlayer.getTraceEffectList()::add);

        var fetterEntries = GameData.getFetterDataEntries();
        for (var avatar : targetPlayer.getAvatars().getAvatars().values()) {
            var dataFetters = fetterEntries.get(avatar.getAvatarId());
            if (dataFetters == null) continue;
            List<Integer> current = avatar.getFetterList();
            if (current == null) {
                avatar.setFetterList(new ArrayList<>(dataFetters));
            } else {
                for (int fetterId : dataFetters) {
                    if (!current.contains(fetterId)) current.add(fetterId);
                }
            }
            avatar.save();
        }
        // carries the owned flycloak, costume and trace effect lists, so one refresh covers them
        targetPlayer.sendPacket(new PacketAvatarDataNotify(targetPlayer));

        // Scene tags. New regions are routinely gated behind these - scene 3 alone ships 635 tags
        // with only 194 valid by default, so leaving them out left a lot of the map switched off.
        GameData.getSceneTagDataMap()
                .values()
                .forEach(
                        tag ->
                                targetPlayer
                                        .getSceneTags()
                                        .computeIfAbsent(tag.getSceneId(), k -> new HashSet<>())
                                        .add(tag.getId()));
        targetPlayer.sendPacket(new PacketPlayerWorldSceneInfoListNotify(targetPlayer));

        // Region access is quest-gated, and a region released after this server's resource set was
        // cut has no quest data here at all - so the quest system cannot finish it and the client
        // keeps the barrier up. Telling the client directly is the only lever available.
        var quests = emu.grasscutter.game.quest.ForcedQuests.allMainQuests();
        emu.grasscutter.game.quest.ForcedQuests.apply(targetPlayer, quests);

        targetPlayer.save();

        CommandHandler.sendMessage(
                sender, translate(sender, "commands.unlockall.success", targetPlayer.getNickname()));
        CommandHandler.sendMessage(
                sender,
                "Also unlocked every scene tag and force-finished every main quest.");
    }
}
