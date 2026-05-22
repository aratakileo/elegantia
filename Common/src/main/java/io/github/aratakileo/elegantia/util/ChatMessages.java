package io.github.aratakileo.elegantia.util;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public final class ChatMessages {
    private ChatMessages() {}

    /**
     * Forces the local player to send a chat message to the server.
     * This will be visible to everyone on the server.
     * Must be called from the main client thread.
     */
    public static void sendGlobal(@NotNull String message) {
        GameUtils.executeOnPlayer(player -> player.connection.sendChat(message));
    }

    /**
     * Displays a message in the local client's chat overlay.
     * This message is entirely local and is never sent to the server.
     */
    public static void sendLocal(@NotNull Component message) {
        GameUtils.player().ifPresent(player -> player.displayClientMessage(message, false));
    }

    /**
     * Displays a message in the local client's chat overlay.
     * This message is entirely local and is never sent to the server.
     */
    public static void sendLocal(@NotNull String message) {
        sendLocal(Component.literal(message));
    }

    /**
     * Displays a message in the local client's chat overlay.
     * This message is entirely local and is never sent to the server.
     */
    public static void sendLocal(@NotNull String message, Object... formats) {
        sendLocal(Component.literal(Strings.format(message, formats)));
    }
}
