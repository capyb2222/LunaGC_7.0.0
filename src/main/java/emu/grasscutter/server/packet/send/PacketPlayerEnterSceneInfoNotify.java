package emu.grasscutter.server.packet.send;

import emu.grasscutter.data.GameData;
import emu.grasscutter.data.binout.OpenConfigEntry;
import emu.grasscutter.data.binout.OpenConfigEntry.AbilityVarSetter;
import emu.grasscutter.data.binout.OpenConfigEntry.TalentParamEntry;
import emu.grasscutter.data.excels.ProudSkillData;
import emu.grasscutter.data.excels.avatar.AvatarSkillDepotData;
import emu.grasscutter.data.excels.avatar.AvatarTalentData;
import emu.grasscutter.game.avatar.Avatar;
import emu.grasscutter.game.entity.EntityAvatar;
import emu.grasscutter.game.inventory.GameItem;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.*;
import emu.grasscutter.net.proto.AbilityAppliedAbilityOuterClass.AbilityAppliedAbility;
import emu.grasscutter.net.proto.AbilitySyncStateInfoOuterClass.AbilitySyncStateInfo;
import emu.grasscutter.net.proto.AvatarEnterSceneInfoOuterClass.AvatarEnterSceneInfo;
import emu.grasscutter.net.proto.MPLevelEntityInfoOuterClass.MPLevelEntityInfo;
import emu.grasscutter.net.proto.PlayerEnterSceneInfoNotifyOuterClass.PlayerEnterSceneInfoNotify;
import emu.grasscutter.net.proto.TeamEnterSceneInfoOuterClass.TeamEnterSceneInfo;
import emu.grasscutter.net.proto.AbilityScalarValueEntryOuterClass.AbilityScalarValueEntry;
import emu.grasscutter.net.proto.AbilityStringOuterClass;
import emu.grasscutter.net.proto.AbilityStringOuterClass.AbilityString;
import emu.grasscutter.utils.Utils;
import java.util.Set;

public class PacketPlayerEnterSceneInfoNotify extends BasePacket {

    private static final Set<Integer> HEXENZIRKEL_IDS = Set.of(
        10000022, 10000029, 10000031, 10000038, 10000041, 10000043, 10000123, 10000128
    );

    public static final Set<Integer> MOONPHASE_IDS = Set.of(
        10000116, 10000119, 10000120, 10000121, 10000122,
        10000124, 10000125, 10000126, 10000127, 10000130
    );

    public PacketPlayerEnterSceneInfoNotify(Player player) {
        super(PacketOpcodes.PlayerEnterSceneInfoNotify);

        AbilityScalarValueEntry scalarValue = AbilityScalarValueEntry.newBuilder()
                .setKey(AbilityStringOuterClass.AbilityString.newBuilder().setHash(Utils.abilityHash("SGV_PlayerTeam_Phlogiston"))
                        .setStr("SGV_PlayerTeam_Phlogiston")
                        .build())
                        .setFloatValue(100)
                .build();
                player.setPhlogistonValue(100);

        long hexCount = player.getTeamManager().getActiveTeam().stream()
                .filter(e -> HEXENZIRKEL_IDS.contains(e.getAvatar().getAvatarId()))
                .count();

        AbilityScalarValueEntry hexLevel = AbilityScalarValueEntry.newBuilder()
                .setKey(AbilityString.newBuilder()
                        .setHash(Utils.abilityHash("SGV_HexenzirkelLevel"))
                        .setStr("SGV_HexenzirkelLevel")
                        .build())
                .setFloatValue(hexCount)
                .build();

        long moonPhaseCount = player.getTeamManager().getActiveTeam().stream()
                .filter(e -> MOONPHASE_IDS.contains(e.getAvatar().getAvatarId()))
                .count();

        AbilityScalarValueEntry moonPhaseLevel = AbilityScalarValueEntry.newBuilder()
                .setKey(AbilityString.newBuilder()
                        .setHash(Utils.abilityHash("SGV_MoonPhaseLevel"))
                        .setStr("SGV_MoonPhaseLevel")
                        .build())
                .setFloatValue(moonPhaseCount)
                .build();

        AbilitySyncStateInfo.Builder teamInfo = AbilitySyncStateInfo.newBuilder()
                .addSgvDynamicValueMap(scalarValue)
                .addSgvDynamicValueMap(hexLevel)
                .addSgvDynamicValueMap(moonPhaseLevel);

        if (moonPhaseCount > 0) {
            teamInfo.addDynamicValueMap(AbilityScalarValueEntry.newBuilder()
                    .setKey(AbilityString.newBuilder()
                            .setHash(Utils.abilityHash("MoonOvergrowPoint_All"))
                            .setStr("MoonOvergrowPoint_All")
                            .build())
                    .setFloatValue(50f)
                    .build());
        }

        AbilitySyncStateInfo phlogiston = teamInfo.build();
        AbilitySyncStateInfo empty = AbilitySyncStateInfo.newBuilder().build();

        PlayerEnterSceneInfoNotify.Builder proto =
                PlayerEnterSceneInfoNotify.newBuilder()
                        .setCurAvatarEntityId(player.getTeamManager().getCurrentAvatarEntity().getId())
                        .setEnterSceneToken(player.getEnterSceneToken());

        proto.setTeamEnterInfo(
                TeamEnterSceneInfo.newBuilder()
                        .setTeamEntityId(player.getTeamManager().getEntity().getId())
                        .setTeamAbilityInfo(phlogiston)
                        .setAbilityControlBlock(player.getTeamManager().getAbilityControlBlock()));
        proto.setMpLevelEntityInfo(
                MPLevelEntityInfo.newBuilder()
                        .setEntityId(player.getWorld().getLevelEntityId())
                        .setAuthorityPeerId(player.getWorld().getHostPeerId())
                        .setAbilityInfo(empty));

        for (EntityAvatar avatarEntity : player.getTeamManager().getActiveTeam()) {
            GameItem weapon = avatarEntity.getAvatar().getWeapon();
            long weaponGuid = weapon != null ? weapon.getGuid() : 0;

            AbilitySyncStateInfo.Builder avatarAbilityInfo = AbilitySyncStateInfo.newBuilder();
            buildAvatarAbilityVars(avatarEntity, avatarAbilityInfo);

            AvatarEnterSceneInfo avatarInfo =
                    AvatarEnterSceneInfo.newBuilder()
                            .setAvatarGuid(avatarEntity.getAvatar().getGuid())
                            .setAvatarEntityId(avatarEntity.getId())
                            .setWeaponGuid(weaponGuid)
                            .setWeaponEntityId(avatarEntity.getWeaponEntityId())
                            .setAvatarAbilityInfo(avatarAbilityInfo)
                            .setWeaponAbilityInfo(AbilitySyncStateInfo.newBuilder())
                            .build();

            proto.addAvatarEnterInfo(avatarInfo);
        }

        this.setData(proto.build());
    }

    public static void buildAvatarAbilityVars(EntityAvatar avatarEntity, AbilitySyncStateInfo.Builder info) {
        Avatar avatar = avatarEntity.getAvatar();

        if (MOONPHASE_IDS.contains(avatar.getAvatarId())) {
            info.addDynamicValueMap(AbilityScalarValueEntry.newBuilder()
                    .setKey(AbilityString.newBuilder()
                            .setHash(Utils.abilityHash("MoonOvergrowPoint_All"))
                            .setStr("MoonOvergrowPoint_All")
                            .build())
                    .setFloatValue(50f)
                    .build());
        }

        for (int proudSkillId : avatar.getProudSkillList()) {
            applyProudSkillVars(proudSkillId, info);
        }

        AvatarSkillDepotData depotData = GameData.getAvatarSkillDepotDataMap().get(avatar.getSkillDepotId());
        if (depotData != null) {
            var skillLevelMap = avatar.getSkillLevelMap();
            depotData.getSkillsAndEnergySkill().forEach(skillId -> {
                var skillCfg = GameData.getAvatarSkillDataMap().get(skillId);
                if (skillCfg == null || skillCfg.getProudSkillGroupId() == 0) return;
                int level = skillLevelMap.getOrDefault(skillId, 1);
                applyProudSkillVars(skillCfg.getProudSkillGroupId() * 100 + level, info);
            });
        }

        for (int talentId : avatar.getTalentIdList()) {
            AvatarTalentData talentData = GameData.getAvatarTalentDataMap().get(talentId);
            if (talentData == null || talentData.getOpenConfig() == null) continue;
            OpenConfigEntry entry = GameData.getOpenConfigEntries().get(talentData.getOpenConfig());
            if (entry == null || entry.getTalentParamEntries() == null) continue;
            for (TalentParamEntry param : entry.getTalentParamEntries()) {
                info.addAppliedAbilities(buildTalentParamAbility(param));
            }
        }
    }

    private static void applyProudSkillVars(int proudSkillId, AbilitySyncStateInfo.Builder info) {
        ProudSkillData skillData = GameData.getProudSkillDataMap().get(proudSkillId);
        if (skillData == null || skillData.getOpenConfig() == null) return;

        OpenConfigEntry entry = GameData.getOpenConfigEntries().get(skillData.getOpenConfig());
        if (entry == null) return;

        boolean hasVarSetters = entry.getAbilityVarSetters() != null;
        boolean hasTalentParams = entry.getTalentParamEntries() != null;
        if (!hasVarSetters && !hasTalentParams) return;

        if (hasVarSetters) {
            float[] paramList = skillData.getParamList();
            for (AbilityVarSetter setter : entry.getAbilityVarSetters()) {
                int idx = setter.getParamIndex();
                if (paramList == null || idx >= paramList.length) continue;
                info.addDynamicValueMap(AbilityScalarValueEntry.newBuilder()
                        .setKey(AbilityString.newBuilder()
                                .setHash(Utils.abilityHash(setter.getVarName()))
                                .setStr(setter.getVarName())
                                .build())
                        .setFloatValue(paramList[idx])
                        .build());
            }
        }

        if (hasTalentParams) {
            for (TalentParamEntry param : entry.getTalentParamEntries()) {
                info.addAppliedAbilities(buildTalentParamAbility(param));
            }
        }
    }

    private static AbilityAppliedAbility buildTalentParamAbility(TalentParamEntry param) {
        return AbilityAppliedAbility.newBuilder()
                .setAbilityName(AbilityString.newBuilder()
                        .setStr(param.getAbilityName())
                        .setHash(Utils.abilityHash(param.getAbilityName()))
                        .build())
                .addOverrideMap(AbilityScalarValueEntry.newBuilder()
                        .setKey(AbilityString.newBuilder()
                                .setStr(param.getParamName())
                                .setHash(Utils.abilityHash(param.getParamName()))
                                .build())
                        .setFloatValue(1.0f)
                        .build())
                .build();
    }
}
