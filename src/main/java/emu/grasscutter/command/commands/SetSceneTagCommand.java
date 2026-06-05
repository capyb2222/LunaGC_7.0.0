package emu.grasscutter.command.commands;

import emu.grasscutter.command.*;
import emu.grasscutter.data.GameData;
import emu.grasscutter.data.excels.scene.SceneTagData;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.server.packet.send.*;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.*;
import lombok.val;

@Command(
        label = "setSceneTag",
        aliases = {"tag"},
        usage = {"<add|remove|unlockall|reset|list> [sceneTagId]"},
        permission = "player.setscenetag",
        permissionTargeted = "player.setscenetag.others")
public final class SetSceneTagCommand implements CommandHandler {
    private final Int2ObjectMap<SceneTagData> sceneTagData = GameData.getSceneTagDataMap();

    @Override
    public void execute(Player sender, Player targetPlayer, List<String> args) {
        if (args.isEmpty()) {
            sendUsageMessage(sender);
            return;
        }

        val actionStr = args.get(0).toLowerCase();
        var value = -1;

        if (args.size() > 1) {
            try {
                value = Integer.parseInt(args.get(1));
            } catch (NumberFormatException ignored) {
                CommandHandler.sendTranslatedMessage(sender, "commands.execution.argument_error");
                return;
            }
        } else {
            if (actionStr.equals("unlockall")) {
                unlockAllSceneTags(targetPlayer);
                return;
            } else if (actionStr.equals("reset") || actionStr.equals("restore")) {
                resetAllSceneTags(targetPlayer);
                return;
            } else if (actionStr.equals("list")) {
                listSceneTags(sender, targetPlayer);
                return;
            } else {
                CommandHandler.sendTranslatedMessage(sender, "commands.execution.argument_error");
                return;
            }
        }

        val userVal = value;

        var sceneData =
                sceneTagData.values().stream().filter(sceneTag -> sceneTag.getId() == userVal).findFirst();
        if (sceneData.isEmpty()) {
            CommandHandler.sendTranslatedMessage(sender, "commands.generic.invalid.id");
            return;
        }
        int scene = sceneData.get().getSceneId();

        switch (actionStr) {
            case "add", "set" -> addSceneTag(targetPlayer, scene, value);
            case "remove", "del" -> removeSceneTag(targetPlayer, scene, value);
            default -> CommandHandler.sendTranslatedMessage(sender, "commands.execution.argument_error");
        }

        CommandHandler.sendTranslatedMessage(sender, "commands.generic.set_to", value, actionStr);
    }

    private void addSceneTag(Player targetPlayer, int scene, int value) {
        targetPlayer.getProgressManager().addSceneTag(scene, value);
    }

    private void removeSceneTag(Player targetPlayer, int scene, int value) {
        targetPlayer.getProgressManager().delSceneTag(scene, value);
    }

    private void unlockAllSceneTags(Player targetPlayer) {
        var allData = sceneTagData.values();

        allData.stream()
                .toList()
                .forEach(
                        sceneTag -> {
                            targetPlayer
                                    .getSceneTags()
                                    .computeIfAbsent(sceneTag.getSceneId(), k -> new HashSet<>());
                            targetPlayer.getSceneTags().get(sceneTag.getSceneId()).add(sceneTag.getId());
                        });

        allData.stream()
                .filter(SceneTagData::isDefaultValid)

                .filter(sceneTag -> sceneTag.getSceneId() == 3)
                .forEach(
                        sceneTag -> {
                            targetPlayer.getSceneTags().get(sceneTag.getSceneId()).remove(sceneTag.getId());
                        });

        this.setSceneTags(targetPlayer);
        CommandHandler.sendMessage(targetPlayer, "All scene tags unlocked.");
    }

    private void resetAllSceneTags(Player targetPlayer) {
        targetPlayer.getSceneTags().clear();

        GameData.getSceneTagDataMap().values().stream()
                .filter(SceneTagData::isDefaultValid)
                .forEach(
                        sceneTag -> {
                            targetPlayer
                                    .getSceneTags()
                                    .computeIfAbsent(sceneTag.getSceneId(), k -> new HashSet<>());
                            targetPlayer.getSceneTags().get(sceneTag.getSceneId()).add(sceneTag.getId());
                        });

        this.setSceneTags(targetPlayer);
        CommandHandler.sendMessage(targetPlayer, "Scene tags reset to defaults.");
    }

    private void listSceneTags(Player sender, Player targetPlayer) {
        int sceneId = targetPlayer.getSceneId();
        Set<Integer> active = targetPlayer.getSceneTags().getOrDefault(sceneId, Set.of());

        List<SceneTagData> sceneTags = sceneTagData.values().stream()
                .filter(t -> t.getSceneId() == sceneId)
                .sorted(Comparator.comparingInt(SceneTagData::getId))
                .toList();

        if (sceneTags.isEmpty()) {
            CommandHandler.sendMessage(sender, "No scene tag data for scene " + sceneId + ".");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Scene ").append(sceneId).append(" tags (").append(active.size()).append(" active):\n");

        List<SceneTagData> activeList  = sceneTags.stream().filter(t ->  active.contains(t.getId())).toList();
        List<SceneTagData> inactiveList = sceneTags.stream().filter(t -> !active.contains(t.getId())).toList();

        if (!activeList.isEmpty()) {
            sb.append("  [ACTIVE]\n");
            for (SceneTagData t : activeList)
                sb.append("    ").append(t.getId()).append("  ").append(t.getSceneTagName()).append("\n");
        }
        if (!inactiveList.isEmpty()) {
            sb.append("  [INACTIVE - use /tag add <id> to enable]\n");
            for (SceneTagData t : inactiveList)
                sb.append("    ").append(t.getId()).append("  ").append(t.getSceneTagName()).append("\n");
        }

        CommandHandler.sendMessage(sender, sb.toString());
    }

    private void setSceneTags(Player targetPlayer) {
        targetPlayer.sendPacket(new PacketPlayerWorldSceneInfoListNotify(targetPlayer));
        targetPlayer.save();
    }
}
