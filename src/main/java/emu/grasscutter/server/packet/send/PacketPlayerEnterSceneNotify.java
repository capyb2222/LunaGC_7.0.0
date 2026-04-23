package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.player.Player.SceneLoadState;
import emu.grasscutter.game.props.EnterReason;
import emu.grasscutter.game.world.Position;
import emu.grasscutter.game.world.data.TeleportProperties;
import emu.grasscutter.net.packet.*;
import emu.grasscutter.net.proto.EnterTypeOuterClass.EnterType;
import emu.grasscutter.net.proto.PlayerEnterSceneNotifyOuterClass.PlayerEnterSceneNotify;
import emu.grasscutter.utils.Utils;

public class PacketPlayerEnterSceneNotify extends BasePacket {

    // Login
    public PacketPlayerEnterSceneNotify(Player player) {
        super(PacketOpcodes.PlayerEnterSceneNotify);

        player.setSceneLoadState(SceneLoadState.LOADING);
        player.setEnterSceneToken(Utils.randomRange(1000, 99999));

        long currentTime = System.currentTimeMillis();

        var proto =
                PlayerEnterSceneNotify.newBuilder()
                        .setSceneId((player.getSceneId() - 49379) ^ 11523)
                        .setPos(player.getPosition().toProto())
                        .setSceneBeginTime((currentTime ^ 27843L) + 16749L)
                        .setType(EnterType.EnterType_ENTER_SELF)
                        .setTargetUid((player.getUid() - 30259) ^ 4145)
                        .setEnterSceneToken((player.getEnterSceneToken() ^ 57361) - 22665)
                        .setWorldLevel(player.getWorldLevel())
                        //.setEnterReason(EnterReason.Login.getValue())
                        //.setIsFirstLoginEnterScene(player.isFirstLoginEnterScene())
                        //.setWorldType(1)
                        .setSceneTransaction(
                                "3-"
                                        + player.getUid()
                                        + "-"
                                        + (int) (System.currentTimeMillis() / 1000)
                                        + "-"
                                        + 18402);

        this.setData(proto);
    }

    public PacketPlayerEnterSceneNotify(
            Player player, EnterType type, EnterReason reason, int newScene, Position newPos) {
        this(player, player, type, reason, newScene, newPos);
    }

    public PacketPlayerEnterSceneNotify(Player player, TeleportProperties teleportProperties) {
        this(player, player, teleportProperties);
    }

    public PacketPlayerEnterSceneNotify(
            Player player,
            Player target,
            EnterType type,
            EnterReason reason,
            int newScene,
            Position newPos) {
        this(
                player,
                target,
                TeleportProperties.builder()
                        .enterType(type)
                        .enterReason(reason)
                        .sceneId(newScene)
                        .teleportTo(newPos)
                        .build());
    }

    // Teleport or go somewhere
    public PacketPlayerEnterSceneNotify(
            Player player, Player target, TeleportProperties teleportProperties) {
        super(PacketOpcodes.PlayerEnterSceneNotify);

        player.setSceneLoadState(SceneLoadState.LOADING);
        player.setEnterSceneToken(Utils.randomRange(1000, 99999));

        long currentTime = System.currentTimeMillis();

        var proto =
                PlayerEnterSceneNotify.newBuilder()
                        //.setPrevSceneId(player.getSceneId())
                        //.setPrevPos(player.getPosition().toProto())
                        .setSceneId((teleportProperties.getSceneId() - 49379) ^ 11523)
                        .setPos(teleportProperties.getTeleportTo().toProto())
                        .setSceneBeginTime((currentTime ^ 27843L) + 16749L)
                        .setType(teleportProperties.getEnterType())
                        .setTargetUid((target.getUid() - 30259) ^ 4145)
                        .setEnterSceneToken((player.getEnterSceneToken() ^ 57361) - 22665)
                        .setWorldLevel(target.getWorld().getWorldLevel())
                        //.setEnterReason(teleportProperties.getEnterReason().getValue())
                        //.setWorldType(1)
                        .setSceneTransaction(
                                teleportProperties.getSceneId()
                                        + "-"
                                        + target.getUid()
                                        + "-"
                                        + (int) (System.currentTimeMillis() / 1000)
                                        + "-"
                                        + 18402);

        // Apply the dungeon ID to the packet if it's a dungeon.
        if (teleportProperties.getDungeonId() != 0) {
            proto.setDungeonId((teleportProperties.getDungeonId() ^ 27544) - 17829);
        }

        this.setData(proto);
    }

    // Go home
    public PacketPlayerEnterSceneNotify(
            Player player, int targetUid, TeleportProperties teleportProperties, boolean other) {
        super(PacketOpcodes.PlayerEnterSceneNotify);

        player.setSceneLoadState(SceneLoadState.LOADING);
        player.setEnterSceneToken(Utils.randomRange(1000, 99999));

        long currentTime = System.currentTimeMillis();

        var proto =
                PlayerEnterSceneNotify.newBuilder()
                        //.setPrevSceneId(player.getSceneId())
                        //.setPrevPos(player.getPosition().toProto())
                        .setSceneId((teleportProperties.getSceneId() - 49379) ^ 11523)
                        .setPos(teleportProperties.getTeleportTo().toProto())
                        .setSceneBeginTime((currentTime ^ 27843L) + 16749L)
                        .setType(other ? EnterType.EnterType_ENTER_OTHER_HOME : EnterType.EnterType_ENTER_SELF_HOME)
                        .setTargetUid((targetUid - 30259) ^ 4145)
                        .setEnterSceneToken((player.getEnterSceneToken() ^ 57361) - 22665)
                        //.setEnterReason(teleportProperties.getEnterReason().getValue())
                        //.setWorldType(64)
                        .setSceneTransaction(
                                teleportProperties.getSceneId()
                                        + "-"
                                        + targetUid
                                        + "-"
                                        + (int) (System.currentTimeMillis() / 1000)
                                        + "-"
                                        + 105092);

        this.setData(proto);
    }
}
