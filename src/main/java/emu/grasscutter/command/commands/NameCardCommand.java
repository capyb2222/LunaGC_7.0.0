package emu.grasscutter.command.commands;

import emu.grasscutter.command.Command;
import emu.grasscutter.command.CommandHandler;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.server.packet.send.PacketSetNameCardRsp;
import emu.grasscutter.server.packet.send.PacketUnlockNameCardNotify;
import emu.grasscutter.utils.FileUtils;
import emu.grasscutter.utils.JsonUtils;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Command(
        label = "namecard",
        aliases = {"card", "setnamecard"},
        usage = {"<nameCardId|clear|default|reset>"},
        permission = "player.namecard",
        permissionTargeted = "player.namecard.others")
public final class NameCardCommand implements CommandHandler {
    private static final int DEFAULT_NAMECARD_ID = 210001;

    private static boolean loadedNameCardIds = false;
    private static final Set<Integer> validNameCardIds = new HashSet<>();

    @Override
    public void execute(Player sender, Player targetPlayer, List<String> args) {
        if (args.size() != 1) {
            sendUsageMessage(sender);
            return;
        }

        String arg = args.get(0).trim().toLowerCase();
        int nameCardId;

        if (arg.equals("clear")
                || arg.equals("default")
                || arg.equals("reset")
                || arg.equals("remove")
                || arg.equals("none")) {
            nameCardId = DEFAULT_NAMECARD_ID;
        } else {
            try {
                nameCardId = Integer.parseInt(arg);
            } catch (NumberFormatException ignored) {
                CommandHandler.sendMessage(sender, "Invalid namecard ID.");
                return;
            }
        }

        if (!isValidNameCardId(nameCardId)) {
            CommandHandler.sendMessage(sender, "Invalid namecard ID: " + nameCardId);
            return;
        }

        if (targetPlayer.getNameCardId() == nameCardId) {
            CommandHandler.sendMessage(
                    sender,
                    "No change needed. Player "
                            + targetPlayer.getUid()
                            + " is already using namecard "
                            + nameCardId
                            + ".");
            return;
        }

        boolean newlyUnlocked = targetPlayer.getNameCardList().add(nameCardId);

        if (newlyUnlocked) {
            targetPlayer.sendPacket(new PacketUnlockNameCardNotify(nameCardId));
        }

        /*
         * Doing this directly instead of targetPlayer.setNameCard(...) so the command can force the change immediately after granting the card.
         */
        targetPlayer.setNameCardId(nameCardId);
        targetPlayer.sendPacket(new PacketSetNameCardRsp(nameCardId));
        targetPlayer.save();

        String action = newlyUnlocked ? "Unlocked and equipped" : "Equipped";

        CommandHandler.sendMessage(
                sender,
                action
                        + " namecard "
                        + nameCardId
                        + " for player "
                        + targetPlayer.getUid()
                        + ".");
    }

    private static boolean isValidNameCardId(int nameCardId) {
        loadNameCardIds();

        if (!validNameCardIds.isEmpty()) {
            return validNameCardIds.contains(nameCardId);
        }

        /*
         * Fallback if NameCardExcelConfigData.json cannot be loaded.
         * Most namecard IDs are in the 210xxx range.
         */
        return nameCardId >= 210000 && nameCardId <= 219999;
    }

    private static synchronized void loadNameCardIds() {
        if (loadedNameCardIds) {
            return;
        }

        loadedNameCardIds = true;

        try {
            var path = FileUtils.getExcelPath("NameCardExcelConfigData.json");

            if (!Files.exists(path)) {
                return;
            }

            List<NameCardExcelEntry> entries = JsonUtils.loadToList(path, NameCardExcelEntry.class);

            for (NameCardExcelEntry entry : entries) {
                if (entry.id > 0) {
                    validNameCardIds.add(entry.id);
                }
            }
        } catch (Exception ignored) {
        }
    }

    private static final class NameCardExcelEntry {
        private int id;
    }
}