package io.github.aratakileo.elegantia.util;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.aratakileo.elegantia.core.environment.Origin;
import io.github.aratakileo.elegantia.core.DeltaTimeSupplier;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;

public final class GameUtils {
    public static final boolean PHYSICAL_CLIENT;

    private GameUtils() {}

    public static @NotNull String gameVersion() {
        return SharedConstants.getCurrentVersion().name();
    }

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

    /**
     * Verification of the execution side.
     * <p>
     * Returns {@code true} only if the current environment is a physical Minecraft client
     * and the call is made from a context where client-side-only interfaces (like rendering)
     * are available.
     * <p>
     * This check is <b>side-safe</b>: it can be used in common code to prevent
     * {@link NoClassDefFoundError} or crashes on dedicated servers.
     */
    public static boolean isClient() {
        return PHYSICAL_CLIENT && RenderSystem.isOnRenderThread();
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
        if (PHYSICAL_CLIENT) return false;

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

    static {
        PHYSICAL_CLIENT = Classes.anyExists(
                "net.minecraft.client.Minecraft",
                "org.lwjgl.glfw.GLFW",
                "net.fabricmc.loader.impl.game.minecraft.MinecraftGameProvider"
        );
    }
}
