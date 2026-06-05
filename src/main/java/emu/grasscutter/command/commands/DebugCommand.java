package emu.grasscutter.command.commands;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.command.*;
import emu.grasscutter.data.GameData;
import emu.grasscutter.data.binout.OpenConfigEntry;
import emu.grasscutter.data.binout.OpenConfigEntry.AbilityVarSetter;
import emu.grasscutter.data.excels.ProudSkillData;
import emu.grasscutter.data.excels.avatar.AvatarSkillDepotData;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.proto.AbilityInvokeArgumentOuterClass.AbilityInvokeArgument;
import emu.grasscutter.net.proto.AbilityInvokeEntryHeadOuterClass.AbilityInvokeEntryHead;
import emu.grasscutter.net.proto.AbilityInvokeEntryOuterClass.AbilityInvokeEntry;
import emu.grasscutter.net.proto.AbilityMetaReInitOverrideMapOuterClass.AbilityMetaReInitOverrideMap;
import emu.grasscutter.net.proto.AbilityScalarValueEntryOuterClass.AbilityScalarValueEntry;
import emu.grasscutter.net.proto.AbilityStringOuterClass.AbilityString;
import emu.grasscutter.net.proto.ForwardTypeOuterClass.ForwardType;
import emu.grasscutter.server.packet.send.PacketAbilityInvocationsNotify;
import java.util.List;

@Command(
        label = "debug",
        usage = "/debug",
        permission = "grasscutter.command.debug",
        targetRequirement = Command.TargetRequirement.NONE)
public final class DebugCommand implements CommandHandler {
    @Override
    public void execute(Player sender, Player targetPlayer, List<String> args) {
        if (sender == null) return;

        if (args.isEmpty()) {
            sender.dropMessage("No arguments provided. (check command for help)");
            return;
        }

        var subCommand = args.get(0);
        args.remove(0);
        switch (subCommand) {
            default -> sender.dropMessage("No arguments provided. (check command for help)");
            case "abilities" -> {
                if (args.isEmpty()) {
                    sender.dropMessage("No arguments provided. (check command for help)");
                    return;
                }

                var scene = sender.getScene();
                var entityId = Integer.parseInt(args.get(0));
                // TODO Might want to allow groupId specification,
                // because there can be more than one entity with
                // the given config ID.
                var entity =
                        args.size() > 1 && args.get(1).equals("config")
                                ? scene.getFirstEntityByConfigId(entityId)
                                : scene.getEntityById(entityId);
                if (entity == null) {
                    sender.dropMessage("Entity not found.");
                    return;
                }

                try {
                    var abilities = entity.getInstancedAbilities();
                    for (var i = 0; i < abilities.size(); i++) {
                        try {
                            var ability = abilities.get(i);
                            Grasscutter.getLogger()
                                    .info(
                                            "Ability #{}: {}; Modifiers: {}",
                                            i,
                                            ability.toString(),
                                            ability.getModifiers().keySet());
                        } catch (Exception exception) {
                            Grasscutter.getLogger().warn("Failed to print ability #{}.", i, exception);
                        }
                    }

                    if (abilities.isEmpty()) {
                        Grasscutter.getLogger().info("No abilities found on {}.", entity.toString());
                    }
                } catch (Exception exception) {
                    Grasscutter.getLogger().warn("Failed to get abilities.", exception);
                }

                sender.dropMessage("Check console for abilities.");
            }
            case "entity" -> {
                var avatarEntity = sender.getTeamManager().getCurrentAvatarEntity();
                if (avatarEntity == null) {
                    sender.dropMessage("No current avatar entity.");
                    return;
                }
                sender.dropMessage("Current avatar entityId=" + avatarEntity.getId()
                    + " avatarId=" + avatarEntity.getAvatar().getAvatarId()
                    + " configId=" + avatarEntity.getConfigId());
            }
            case "setvar" -> {

                if (args.size() < 3) {
                    sender.dropMessage("Usage: /debug setvar <entityId> <varName> <floatValue> [abilityIdx]");
                    return;
                }
                int entityId = Integer.parseInt(args.get(0));
                String varName = args.get(1);
                float value = Float.parseFloat(args.get(2));
                int abilityIdx = args.size() > 3 ? Integer.parseInt(args.get(3)) : 1;

                var entry = AbilityScalarValueEntry.newBuilder()
                    .setKey(AbilityString.newBuilder().setStr(varName).build())
                    .setFloatValue(value)
                    .build();
                var overrideMap = AbilityMetaReInitOverrideMap.newBuilder()
                    .addOverrideMap(entry)
                    .build();
                var head = AbilityInvokeEntryHead.newBuilder()
                    .setInstancedAbilityId(abilityIdx)
                    .build();
                var invoke = AbilityInvokeEntry.newBuilder()
                    .setEntityId(entityId)
                    .setArgumentType(AbilityInvokeArgument.AbilityInvokeArgument_ABILITY_META_REINIT_OVERRIDEMAP)
                    .setForwardType(ForwardType.ForwardType_FORWARD_TO_ALL)
                    .setHead(head)
                    .setAbilityData(overrideMap.toByteString())
                    .build();

                sender.sendPacket(new PacketAbilityInvocationsNotify(invoke));

                var invokeOverride = AbilityInvokeEntry.newBuilder()
                    .setEntityId(entityId)
                    .setArgumentType(AbilityInvokeArgument.AbilityInvokeArgument_ABILITY_META_OVERRIDE_PARAM)
                    .setForwardType(ForwardType.ForwardType_FORWARD_TO_ALL)
                    .setHead(head)
                    .setAbilityData(entry.toByteString())
                    .build();
                sender.sendPacket(new PacketAbilityInvocationsNotify(invokeOverride));

                sender.dropMessage("Sent REINIT_OVERRIDEMAP+OVERRIDE_PARAM: entity=" + entityId + " ability=" + abilityIdx + " " + varName + "=" + value);
            }
            case "dynamicmap" -> {
                var avatar = sender.getTeamManager().getCurrentAvatarEntity().getAvatar();
                var skillLevelMap = avatar.getSkillLevelMap();
                var depotData = GameData.getAvatarSkillDepotDataMap().get(avatar.getSkillDepotId());
                int total = 0;

                Grasscutter.getLogger().info("=== dynamicValueMap for avatar {} (depot {}) ===",
                    avatar.getAvatarId(), avatar.getSkillDepotId());

                for (int proudSkillId : avatar.getProudSkillList()) {
                    total += dumpProudSkillVars(proudSkillId, "passive");
                }

                if (depotData != null) {
                    for (int skillId : depotData.getSkillsAndEnergySkill().toArray()) {
                        var skillCfg = GameData.getAvatarSkillDataMap().get(skillId);
                        if (skillCfg == null || skillCfg.getProudSkillGroupId() == 0) continue;
                        int level = skillLevelMap.getOrDefault(skillId, 1);
                        int proudId = skillCfg.getProudSkillGroupId() * 100 + level;
                        total += dumpProudSkillVars(proudId, "skill " + skillId + " lv" + level);
                    }
                }

                Grasscutter.getLogger().info("=== total entries: {} ===", total);
                sender.dropMessage("Check console for dynamicValueMap (" + total + " entries).");
            }
        }
    }

    private static int dumpProudSkillVars(int proudSkillId, String label) {
        ProudSkillData skillData = GameData.getProudSkillDataMap().get(proudSkillId);
        if (skillData == null || skillData.getOpenConfig() == null) return 0;
        OpenConfigEntry entry = GameData.getOpenConfigEntries().get(skillData.getOpenConfig());
        if (entry == null || entry.getAbilityVarSetters() == null) return 0;

        float[] paramList = skillData.getParamList();
        int count = 0;
        for (AbilityVarSetter setter : entry.getAbilityVarSetters()) {
            int idx = setter.getParamIndex();
            float val = (paramList != null && idx < paramList.length) ? paramList[idx] : Float.NaN;
            Grasscutter.getLogger().info(
                "  [{}] openConfig={} ability={} var={} paramIdx={} value={}",
                label, skillData.getOpenConfig(), setter.getAbilityName(),
                setter.getVarName(), idx, val);
            count++;
        }
        return count;
    }
}
