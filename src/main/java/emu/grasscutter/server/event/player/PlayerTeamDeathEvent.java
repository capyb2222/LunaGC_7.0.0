package emu.grasscutter.server.event.player;

import emu.grasscutter.game.entity.EntityAvatar;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.server.event.types.PlayerEvent;
import lombok.Getter;

public final class PlayerTeamDeathEvent extends PlayerEvent {
    @Getter private final EntityAvatar selectedAvatar;

    public PlayerTeamDeathEvent(Player player, EntityAvatar selectedAvatar) {
        super(player);

        this.selectedAvatar = selectedAvatar;
    }
}
