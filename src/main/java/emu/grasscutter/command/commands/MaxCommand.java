package emu.grasscutter.command.commands;

import emu.grasscutter.command.Command;
import emu.grasscutter.command.CommandHandler;
import emu.grasscutter.data.GameData;
import emu.grasscutter.game.avatar.Avatar;
import emu.grasscutter.game.inventory.GameItem;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.props.FightProperty;
import emu.grasscutter.server.packet.send.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Brings a character all the way up in one go - level, ascension, talents, constellations, passives,
 * friendship, and the weapon they are holding.
 *
 * <p>{@code /give avatar} can already do most of this, but only while handing you a new copy. This
 * works on the character you already have and are standing in, which is what you want when a
 * freshly granted character needs to be usable for a real fight.
 *
 * <p>Caps come from the resources rather than being written down here, so a three-star weapon stops
 * at its own ceiling instead of being pushed to a level it cannot reach.
 */
@Command(
        label = "max",
        aliases = {"maxavatar", "maxchar"},
        usage = {
            "", // the character you are currently controlling
            "all" // every character you own
        },
        permission = "player.max",
        permissionTargeted = "player.max.others")
public final class MaxCommand implements CommandHandler {

    private static final int MAX_AVATAR_LEVEL = 90;
    private static final int MAX_CONSTELLATION = 6;
    private static final int MAX_FETTER_LEVEL = 10;
    /** Refinement is stored zero-based, so 4 is R5. */
    private static final int MAX_REFINEMENT = 4;
    /** Fallback talent ceiling for a skill whose level table is missing. */
    private static final int DEFAULT_MAX_TALENT = 10;

    @Override
    public void execute(Player sender, Player targetPlayer, List<String> args) {
        boolean all = args.size() == 1 && args.get(0).equalsIgnoreCase("all");
        if (!args.isEmpty() && !all) {
            sendUsageMessage(sender);
            return;
        }

        var targets = new ArrayList<Avatar>();
        if (all) {
            targetPlayer.getAvatars().forEach(targets::add);
        } else {
            var entity = targetPlayer.getTeamManager().getCurrentAvatarEntity();
            if (entity == null) {
                CommandHandler.sendMessage(sender, "No character is currently active.");
                return;
            }
            targets.add(entity.getAvatar());
        }

        if (targets.isEmpty()) {
            CommandHandler.sendMessage(sender, "No characters to max out.");
            return;
        }

        for (Avatar avatar : targets) {
            maxAvatar(targetPlayer, avatar);
        }

        // The new max HP does not fill itself in, and a character that arrives at level 90 on a
        // level 1 health bar is not what anyone means by maxed.
        healActiveTeam(targetPlayer);

        if (all) {
            CommandHandler.sendMessage(sender, "Maxed out " + targets.size() + " characters.");
        } else {
            CommandHandler.sendMessage(
                    sender, "Maxed out " + targets.get(0).getAvatarData().getName() + ".");
        }
    }

    private void maxAvatar(Player player, Avatar avatar) {
        avatar.setLevel(MAX_AVATAR_LEVEL);
        avatar.setPromoteLevel(Avatar.getMinPromoteLevel(MAX_AVATAR_LEVEL));

        // Talents. Constellations 3 and 5 add their +3 separately, as a bonus on top of the stored
        // level, so the stored level wants the data's own ceiling and not a higher number - which
        // setSkillLevel would reject anyway.
        var depot = avatar.getSkillDepot();
        if (depot != null) {
            depot
                    .getSkillsAndEnergySkill()
                    .forEach(
                            id -> {
                                var levels = GameData.getAvatarSkillLevels(id);
                                int max =
                                        levels == null || levels.isEmpty()
                                                ? DEFAULT_MAX_TALENT
                                                : levels.intStream().max().orElse(DEFAULT_MAX_TALENT);
                                avatar.setSkillLevel(id, max);
                            });
        }

        avatar.forceConstellationLevel(MAX_CONSTELLATION);
        avatar.recalcConstellations();

        avatar.setFetterLevel(MAX_FETTER_LEVEL);

        maxWeapon(player, avatar);

        // recalcStats rebuilds the passive list from the promote level set above, so the ascension
        // passives come along without being unlocked by hand.
        avatar.recalcStats(true);
        avatar.save();

        player.sendPacket(new PacketAvatarPropNotify(avatar));
        player.sendPacket(new PacketProudSkillChangeNotify(avatar));
        player.sendPacket(new PacketAvatarFetterDataNotify(avatar));
    }

    private void maxWeapon(Player player, Avatar avatar) {
        GameItem weapon = avatar.getWeapon();
        if (weapon == null || weapon.getItemData() == null) {
            return;
        }

        // Walk the promote table rather than assuming 90/6: a three-star weapon tops out at 70.
        int promoteId = weapon.getItemData().getWeaponPromoteId();
        int maxPromoteLevel = weapon.getPromoteLevel();
        int maxLevel = weapon.getLevel();
        for (int promote = 0; ; promote++) {
            var data = GameData.getWeaponPromoteData(promoteId, promote);
            if (data == null) break;
            maxPromoteLevel = promote;
            maxLevel = Math.max(maxLevel, data.getUnlockMaxLevel());
        }

        weapon.setPromoteLevel(maxPromoteLevel);
        weapon.setLevel(maxLevel);

        // Only weapons that actually carry a refinable affix; the rest have nothing to refine and
        // a stored refinement would just be a number the client never shows.
        var affixes = weapon.getItemData().getSkillAffix();
        if (affixes != null && affixes.length > 0 && affixes[0] != 0) {
            weapon.setRefinement(MAX_REFINEMENT);
        }

        weapon.save();
        player.sendPacket(new PacketStoreItemChangeNotify(weapon));
    }

    private void healActiveTeam(Player player) {
        player
                .getTeamManager()
                .getActiveTeam()
                .forEach(
                        entity -> {
                            entity.setFightProperty(
                                    FightProperty.FIGHT_PROP_CUR_HP,
                                    entity.getFightProperty(FightProperty.FIGHT_PROP_MAX_HP));
                            entity
                                    .getWorld()
                                    .broadcastPacket(
                                            new PacketAvatarFightPropUpdateNotify(
                                                    entity.getAvatar(), FightProperty.FIGHT_PROP_CUR_HP));
                        });
    }
}
