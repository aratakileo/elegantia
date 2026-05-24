package io.github.aratakileo.elegantia.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.aratakileo.elegantia.common.environment.EnvironmentType;
import io.github.aratakileo.elegantia.common.environment.Loader;
import io.github.aratakileo.elegantia.common.environment.Origin;
import io.github.aratakileo.elegantia.core.DeltaTimeSupplier;
import io.github.aratakileo.elegantia.core.util.Exceptions;
import io.github.aratakileo.elegantia.core.util.Strings;
import io.netty.util.concurrent.FastThreadLocalThread;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;

public final class GameUtils {
    private GameUtils() {}

    public static @NotNull Optional<LocalPlayer> player() {
        return Optional.ofNullable(Minecraft.getInstance().player);
    }

    /**
     * Schedules the given action to run synchronously on the main client thread
     * if a local player instance is currently present in the world.
     * <p>
     * The {@link Minecraft#execute} method ensures that the consumer
     * runs inside the Render thread, preventing concurrent modification issues
     * when called from network or background threads. The action will execute
     * immediately if called from the main thread, or during the next client tick
     * if submitted asynchronously.
     * </p>
     *
     * @param consumer the action to be performed on the active player instance
     * @throws IllegalStateException if invoked on a dedicated server environment while running in an IDE
     */
    public static void executeOnPlayer(@NotNull Consumer<LocalPlayer> consumer) {
        if (reportClientCallOnServer("executeOnPlayer")) return;

        Minecraft.getInstance().execute(() -> {
            // Checking presence inside the scheduled task prevents a race condition
            // where the player could disconnect between scheduling and execution.
            player().ifPresent(consumer);
        });
    }

    public static void execute(@NotNull Runnable task) {
        Minecraft.getInstance().execute(task);
    }

    public static boolean isOnRenderThread() {
        return RenderSystem.isOnRenderThread();
    }

    public static boolean isOnNetworkThread() {
        final var currentThread = Thread.currentThread();

        if (currentThread instanceof FastThreadLocalThread) return true;

        final var threadName = currentThread.getName();

        return threadName.startsWith("Netty ") && threadName.contains("IO");
    }

    public static boolean isOnClientThread() {
        return isOnRenderThread() || isOnNetworkThread();
    }

    public static boolean hudShouldBeDrawn() {
        return !reportClientCallOnServer("hudShouldBeDrawn")
                && !Minecraft.getInstance().options.hideGui
                && !Minecraft.getInstance().getDebugOverlay().showDebugScreen();
    }

    /**
     * @return a GUI delta time in milliseconds
     */
    public static long deltaTime() {
        if (reportClientCallOnServer("deltaTime")) return 0;

        return ((DeltaTimeSupplier)Minecraft.getInstance()).deltaTime();
    }

    private static boolean reportClientCallOnServer(@NotNull String methodName) {
        if (Loader.environment() == EnvironmentType.CLIENT) return false;

        Exceptions.throwOrLog(
                Origin.ELEGANTIA,
                IllegalStateException::new,
                Strings.format(
                        "client side method `{}` was illegally invoked on a server environment!",
                        methodName
                )
        );

        return true;
    }
}
