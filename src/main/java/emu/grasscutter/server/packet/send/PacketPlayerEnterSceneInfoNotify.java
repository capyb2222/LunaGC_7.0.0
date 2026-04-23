package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.entity.EntityAvatar;
import emu.grasscutter.game.inventory.GameItem;
import emu.grasscutter.game.props.FightProperty;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.*;
import emu.grasscutter.net.proto.AbilitySyncStateInfoOuterClass.AbilitySyncStateInfo;
import emu.grasscutter.net.proto.AvatarEnterSceneInfoOuterClass.AvatarEnterSceneInfo;
import emu.grasscutter.net.proto.MPLevelEntityInfoOuterClass.MPLevelEntityInfo;
import emu.grasscutter.net.proto.PlayerEnterSceneInfoNotifyOuterClass.PlayerEnterSceneInfoNotify;
import emu.grasscutter.net.proto.SceneWeaponInfoOuterClass.SceneWeaponInfo;
import emu.grasscutter.net.proto.TeamEnterSceneInfoOuterClass.TeamEnterSceneInfo;
import emu.grasscutter.net.proto.AbilityScalarValueEntryOuterClass.AbilityScalarValueEntry;
import emu.grasscutter.net.proto.AbilityScalarTypeOuterClass.AbilityScalarType;
import emu.grasscutter.net.proto.AbilityStringOuterClass;
import emu.grasscutter.net.proto.AbilityStringOuterClass.AbilityString;
import emu.grasscutter.utils.Utils;

public class PacketPlayerEnterSceneInfoNotify extends BasePacket {

    public PacketPlayerEnterSceneInfoNotify(Player player) {
        super(PacketOpcodes.PlayerEnterSceneInfoNotify);



        AbilityScalarValueEntry scalarValue = AbilityScalarValueEntry.newBuilder()
                .setKey(AbilityStringOuterClass.AbilityString.newBuilder().setHash(Utils.abilityHash("SGV_PlayerTeam_Phlogiston"))
                        .setStr("SGV_PlayerTeam_Phlogiston")
                        .build())
                        .setFloatValue(100)
                .build();
                player.setPhlogistonValue(100);

        AbilitySyncStateInfo phlogiston = AbilitySyncStateInfo.newBuilder().addSgvDynamicValueMap(scalarValue).build();
        AbilitySyncStateInfo empty = AbilitySyncStateInfo.newBuilder().build();

        PlayerEnterSceneInfoNotify.Builder proto =
                PlayerEnterSceneInfoNotify.newBuilder()
                        .setCurAvatarEntityId(player.getTeamManager().getCurrentAvatarEntity().getId())
                        .setEnterSceneToken(player.getEnterSceneToken());

        proto.setTeamEnterInfo(
                TeamEnterSceneInfo.newBuilder()
                        .setTeamEntityId(player.getTeamManager().getEntity().getId()) // 150995833
                        .setTeamAbilityInfo(phlogiston)
                        .setAbilityControlBlock(player.getTeamManager().getAbilityControlBlock()));
        proto.setMpLevelEntityInfo(
                MPLevelEntityInfo.newBuilder()
                        .setEntityId(player.getWorld().getLevelEntityId()) // 184550274
                        .setAuthorityPeerId(player.getWorld().getHostPeerId())
                        .setAbilityInfo(empty));

        for (EntityAvatar avatarEntity : player.getTeamManager().getActiveTeam()) {
            GameItem weapon = avatarEntity.getAvatar().getWeapon();

            long weaponGuid = weapon != null ? weapon.getGuid() : 0;


            AvatarEnterSceneInfo avatarInfo =
                    AvatarEnterSceneInfo.newBuilder()
                            .setAvatarGuid(avatarEntity.getAvatar().getGuid())
                            .setAvatarEntityId(avatarEntity.getId())
                            .setWeaponGuid(weaponGuid)
                            .setWeaponEntityId(avatarEntity.getWeaponEntityId())
                            .setAvatarAbilityInfo(AbilitySyncStateInfo.newBuilder())
                            .setWeaponAbilityInfo(AbilitySyncStateInfo.newBuilder())
                            .build();

            proto.addAvatarEnterInfo(avatarInfo);
        }

        this.setData(proto.build());
    }
}
