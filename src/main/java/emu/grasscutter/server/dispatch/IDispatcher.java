package emu.grasscutter.server.dispatch;

import static emu.grasscutter.config.Configuration.DISPATCH_INFO;

import com.google.gson.*;
import emu.grasscutter.utils.Crypto;
import emu.grasscutter.utils.JsonAdapters.ByteArrayAdapter;
import emu.grasscutter.utils.objects.JObject;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import org.java_websocket.WebSocket;
import org.slf4j.Logger;

public interface IDispatcher {
    Gson JSON =
            new GsonBuilder()
                    .disableHtmlEscaping()
                    .registerTypeAdapter(byte[].class, new ByteArrayAdapter())
                    .registerTypeAdapter(JObject.class, new JObject.Adapter())
                    .create();

    Function<JsonElement, JsonObject> DEFAULT_PARSER =
            (packet) -> IDispatcher.decode(packet, JsonObject.class);

    static JsonObject decode(JsonElement element) {
        return IDispatcher.decode(element, JsonObject.class);
    }

    static <T> T decode(JsonElement element, Class<T> type) {
        if (element.isJsonObject()) {
            return JSON.fromJson(element, type);
        } else {
            var data = element.getAsString();

            // Check if the element starts and ends with quotes.
            if (data.startsWith("\"") && data.endsWith("\"")) {
                // Remove the quotes.
                data = data.substring(1, data.length() - 1);
            }

            // Un-escape the data.
            data = data.replaceAll("\\\\\"", "\"");
            data = data.replaceAll("\\\\", "");

            // De-serialize the data.
            return JSON.fromJson(data, type);
        }
    }

    default <T> T await(
            JsonObject request, int requestId, int responseId, Function<JsonElement, T> parser) {
        // Perform the setup for the request.
        var future = this.async(request, requestId, responseId, parser);

        try {
            // Try to return the value.
            return future.get(5L, TimeUnit.SECONDS);
        } catch (Exception ignored) {
            return null;
        }
    }

    default CompletableFuture<JsonObject> async(JsonObject request, int requestId, int responseId) {
        return this.async(request, requestId, responseId, DEFAULT_PARSER);
    }

    default <T> CompletableFuture<T> async(
            JsonObject request, int requestId, int responseId, Function<JsonElement, T> parser) {
        // Create the future.
        var future = new CompletableFuture<T>();
        // Listen for the response.
        this.registerCallback(responseId, packet -> future.complete(parser.apply(packet)));
        // Broadcast the packet.
        this.sendMessage(requestId, request);

        return future;
    }

    void sendMessage(int packetId, Object message);

    default JsonObject decodeMessage(byte[] message) {
        // Decrypt the message.
        Crypto.xor(message, DISPATCH_INFO.encryptionKey);
        // Deserialize the message.
        return JSON.fromJson(new String(message, StandardCharsets.UTF_8), JsonObject.class);
    }

    default JsonObject encodeMessage(int packetId, Object message) {
        // Create a message from the message data.
        var serverMessage = new JsonObject();
        serverMessage.addProperty("packetId", packetId);
        serverMessage.addProperty("message", JSON.toJson(message));

        return serverMessage;
    }

    default void handleMessage(WebSocket socket, byte[] messageData) {
        // Decode the message.
        var decoded = this.decodeMessage(messageData);
        if (decoded == null) {
            this.getLogger().warn("Received invalid message.");
            socket.close();
            return;
        }

        // Get the packet ID.
        var packetId = decoded.get("packetId").getAsInt();
        // Get the packet data.
        var packetData = decoded.get("message");

        // Check to see if the client has authenticated.
        if (packetId != PacketIds.LoginNotify) {
            if (socket.getAttachment() instanceof Boolean authenticated) {
                if (!authenticated) {
                    this.getLogger().warn("Received packet ID {} from unauthenticated client.", packetId);
                    socket.close();
                    return;
                }
            } else return;
        }

        try {
            // Check if the packet ID is registered.
            if (this.getHandlers().containsKey(packetId)) {
                // Get the handler.
                var handler = this.getHandlers().get(packetId);
                // Handle the packet.
                handler.accept(socket, packetData);
            }

            // Check if the packet ID has callbacks.
            if (this.getCallbacks().containsKey(packetId)) {
                // Get the callbacks.
                var callbacks = this.getCallbacks().get(packetId);
                // Call the callbacks.
                callbacks.forEach(callback -> callback.accept(packetData));
                callbacks.clear();
            }
        } catch (Exception exception) {
            this.getLogger().warn("Exception occurred while handling packet {}.", packetId);
            exception.printStackTrace();
        }
    }

    default void registerHandler(int packetId, BiConsumer<WebSocket, JsonElement> handler) {
        // Check if the packet ID is already registered.
        if (this.getHandlers().containsKey(packetId))
            throw new IllegalArgumentException("Packet ID already registered.");

        // Register the handler.
        this.getHandlers().put(packetId, handler);
    }

    default void registerCallback(int packetId, Consumer<JsonElement> callback) {
        // Check if the packet ID has a list for callbacks.
        if (!this.getCallbacks().containsKey(packetId))
            this.getCallbacks().put(packetId, new LinkedList<>());

        // Register the callback.
        this.getCallbacks().get(packetId).add(callback);
    }

    default void sendServerMessage(byte[] data, boolean binary) {
        var message =
                new JObject()
                        .add("binary", binary)
                        .add("data", Base64.getEncoder().encodeToString(data))
                        .gson();

        this.sendMessage(PacketIds.ServerMessageNotify, message);
    }

    default void sendServerMessage(String data) {
        this.sendServerMessage(data.getBytes(), false);
    }

    default void sendServerMessage(byte[] data) {
        this.sendServerMessage(data, true);
    }

    default void sendServerMessage(Object data) {
        this.sendServerMessage(JSON.toJson(data));
    }

    Logger getLogger();

    Map<Integer, BiConsumer<WebSocket, JsonElement>> getHandlers();

    Map<Integer, List<Consumer<JsonElement>>> getCallbacks();
}
