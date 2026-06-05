package emu.grasscutter.plugin.api;

import emu.grasscutter.game.avatar.Avatar;
import emu.grasscutter.game.entity.EntityAvatar;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.props.*;
import emu.grasscutter.game.world.Position;
import emu.grasscutter.net.packet.BasePacket;
import emu.grasscutter.net.proto.EnterTypeOuterClass.EnterType;
import emu.grasscutter.server.packet.send.*;

public interface PlayerHook {

    Player getPlayer();

    default void kick() {
        this.getPlayer().getSession().close();
    }

    default void changeScenes(int sceneId) {
        this.getPlayer()
                .getWorld()
                .transferPlayerToScene(this.getPlayer(), sceneId, this.getPlayer().getPosition());
    }

    default void updateFightProperty(FightProperty property) {
        this.broadcastPacketToWorld(
                new PacketAvatarFightPropUpdateNotify(this.getCurrentAvatar(), property));
    }

    default void broadcastPacketToWorld(BasePacket packet) {
        this.getPlayer().getWorld().broadcastPacket(packet);
    }

    default void setHealth(float health) {
        this.getCurrentAvatarEntity().setFightProperty(FightProperty.FIGHT_PROP_CUR_HP, health);
        this.updateFightProperty(FightProperty.FIGHT_PROP_CUR_HP);
    }

    default void reviveAvatar(Avatar avatar) {
        this.broadcastPacketToWorld(new PacketAvatarLifeStateChangeNotify(avatar));
    }

    default void teleport(Position position) {
        this.getPlayer().getPosition().set(position);
        this.getPlayer()
                .sendPacket(
                        new PacketPlayerEnterSceneNotify(
                                this.getPlayer(),
                                EnterType.EnterType_ENTER_JUMP,
                                EnterReason.TransPoint,
                                this.getPlayer().getSceneId(),
                                position));
        this.getPlayer().sendPacket(new PacketEnterScenePeerNotify(this.getPlayer()));
    }

    default float getMaxHealth() {
        return this.getCurrentAvatarEntity().getFightProperty(FightProperty.FIGHT_PROP_MAX_HP);
    }

    default EntityAvatar getCurrentAvatarEntity() {
        return this.getPlayer().getTeamManager().getCurrentAvatarEntity();
    }

    default Avatar getCurrentAvatar() {
        return this.getCurrentAvatarEntity().getAvatar();
    }
}
