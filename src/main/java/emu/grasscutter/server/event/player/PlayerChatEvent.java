package emu.grasscutter.server.event.player;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.server.event.Cancellable;
import emu.grasscutter.server.event.types.PlayerEvent;
import javax.annotation.Nullable;
import lombok.*;

@Getter
@Setter
public final class PlayerChatEvent extends PlayerEvent implements Cancellable {
    private String message;

    @Nullable private Player to;

    @Nullable private Integer channelId;

    public PlayerChatEvent(Player player, String message, @Nullable Player to) {
        super(player);

        this.message = message;
        this.to = to;
    }

    public PlayerChatEvent(Player player, int emoteId, @Nullable Player to) {
        super(player);

        this.message = String.valueOf(emoteId);
        this.to = to;
    }

    public PlayerChatEvent(Player player, String message, int channelId) {
        super(player);

        this.message = message;
        this.channelId = channelId;
    }

    public PlayerChatEvent(Player player, int emoteId, int channelId) {
        super(player);

        this.message = String.valueOf(emoteId);
        this.channelId = channelId;
    }

    public int getTargetUid() {
        return this.to == null ? -1 : this.to.getUid();
    }

    public int getMessageAsInt() {
        try {
            return Integer.parseInt(this.message);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public int getChannel() {
        return this.channelId == null ? -1 : this.channelId;
    }
}
