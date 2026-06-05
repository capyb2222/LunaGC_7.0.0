package emu.grasscutter.server.game;

import static emu.grasscutter.config.Configuration.GAME_INFO;
import static emu.grasscutter.config.Configuration.SERVER;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.Grasscutter.ServerDebugMode;
import emu.grasscutter.net.packet.*;
import emu.grasscutter.server.event.game.ReceivePacketEvent;
import emu.grasscutter.server.game.GameSession.SessionState;
import it.unimi.dsi.fastutil.ints.*;
public final class GameServerPacketHandler {

    private final Int2ObjectMap<PacketHandler> handlers;

    public GameServerPacketHandler(Class<? extends PacketHandler> handlerClass) {
        this.handlers = new Int2ObjectOpenHashMap<>();

        this.registerHandlers(handlerClass);
    }

    public void registerPacketHandler(Class<? extends PacketHandler> handlerClass) {
        try {
            var opcode = handlerClass.getAnnotation(Opcodes.class);
            if (opcode == null || opcode.disabled() || opcode.value() <= 0) {
                return;
            }

            var packetHandler = handlerClass.getDeclaredConstructor().newInstance();
            this.handlers.put(opcode.value(), packetHandler);
        } catch (Exception e) {
            Grasscutter.getLogger()
                    .warn("Unable to register handler {}.", handlerClass.getSimpleName(), e);
        }
    }

    public void registerHandlers(Class<? extends PacketHandler> handlerClass) {
        var handlerClasses = Grasscutter.reflector.getSubTypesOf(handlerClass);
        for (var obj : handlerClasses) {
            this.registerPacketHandler(obj);
        }

        Grasscutter.getLogger()
                .debug("Registered " + this.handlers.size() + " " + handlerClass.getSimpleName() + "s");
    }

    public void handle(GameSession session, int opcode, byte[] header, byte[] payload) {
        PacketHandler handler = this.handlers.get(opcode);

        if (handler != null) {
            try {

                SessionState state = session.getState();

                if (opcode == PacketOpcodes.PingReq) {

                } else if (opcode == PacketOpcodes.GetPlayerTokenReq) {
                    if (state != SessionState.WAITING_FOR_TOKEN) {
                        return;
                    }
                } else if (state == SessionState.ACCOUNT_BANNED) {
                    session.close();
                    return;
                } else if (opcode == PacketOpcodes.PlayerLoginReq) {
                    if (state != SessionState.WAITING_FOR_LOGIN) {
                        return;
                    }
                } else if (opcode == PacketOpcodes.SetPlayerBornDataReq) {
                    if (state != SessionState.PICKING_CHARACTER) {
                        return;
                    }
                } else {
                    if (state != SessionState.ACTIVE) {
                        return;
                    }
                }

                ReceivePacketEvent event = new ReceivePacketEvent(session, opcode, payload);
                event.call();
                if (!event.isCanceled())
                handler.handle(session, header, event.getPacketData());
            } catch (Exception ex) {

                ex.printStackTrace();
            }
            return;
        }

    }

    private static boolean shouldDump(GameSession session, int opcode) {
        if (PacketOpcodes.BANNED_PACKETS.contains(opcode)) return false;
        return switch (GAME_INFO.logPackets) {
            case ALL -> !PacketOpcodesUtils.LOOP_PACKETS.contains(opcode) || GAME_INFO.isShowLoopPackets;
            case WHITELIST -> SERVER.debugWhitelist.contains(opcode);
            case BLACKLIST -> !SERVER.debugBlacklist.contains(opcode);
            default -> false;
        };
    }

}
