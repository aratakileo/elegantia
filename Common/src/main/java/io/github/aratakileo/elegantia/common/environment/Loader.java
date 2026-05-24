package io.github.aratakileo.elegantia.common.environment;

import io.github.aratakileo.elegantia.core.Result;
import io.github.aratakileo.elegantia.core.reflection.ClassContainer;
import io.github.aratakileo.elegantia.core.util.Classes;
import io.github.aratakileo.elegantia.core.util.Exceptions;
import io.github.aratakileo.elegantia.core.LazySafeInitializer;
import net.minecraft.SharedConstants;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public enum Loader {
    FABRIC("Fabric"),
    QUILT("Quilt"),
    FORGE("Forge"),
    NEOFORGE("NeoForge"),
    VANILLA("Minecraft");

    private static final LazySafeInitializer<EnvironmentType> ENVIRONMENT;
    private static final LazySafeInitializer<Loader> LOADER;

    private static volatile ProfileProvider PROFILE_PROVIDER = null;

    private static final ConcurrentHashMap<String, ModProfile> PROFILE_CACHE = new ConcurrentHashMap<>();

    private final String displayName;

    Loader(@NotNull String displayName) {
        this.displayName = displayName;
    }

    public @NotNull String displayName() {
        return displayName;
    }

    public @NotNull Origin origin() {
        return Origin.from(displayName.toLowerCase());
    }

    public boolean fabricBased() {
        return this == FABRIC || this == QUILT;
    }

    public boolean forgeBased() {
        return this == FORGE || this == NEOFORGE;
    }

    public static @NotNull Loader current() {
        return LOADER.unwrap("failed to find out the loader type");
    }

    /**
     * In case if you need to check if loader platform is defined but don't need to actually try to define it
     * @return {@code true} if the loader platform is defined and else otherwise
     */
    public static boolean currentDefined() {
        return LOADER.initialized();
    }

    public static @NotNull String gameVersion() {
        return SharedConstants.getCurrentVersion().name();
    }

    public static @NotNull ModProfile modUnwrap(@NotNull Origin origin) {
        return modOptional(origin.key).orElseThrow();
    }

    public static @NotNull ModProfile modUnwrap(@NotNull String id) {
        return modOptional(id).orElseThrow();
    }

    public static @NotNull Result<ModProfile> modResult(@NotNull Origin origin) {
        return modResult(origin.key);
    }

    public static @NotNull Result<ModProfile> modResult(@NotNull String id) {
        return Result.fromOptional(
                modOptional(id),
                IllegalArgumentException::new,
                "could not find mod with id `{}`",
                id
        );
    }

    public static @NotNull Optional<ModProfile> modOptional(@NotNull Origin origin) {
        return modOptional(origin.key);
    }

    public static @NotNull Optional<ModProfile> modOptional(@NotNull String id) {
        if (PROFILE_PROVIDER == null)
            throw new IllegalStateException(ProfileProvider.class.getName() + " was accessed before initialization!");

        final var profile = PROFILE_CACHE.computeIfAbsent(
                id,
                modId -> PROFILE_PROVIDER.modOptional(modId).orElse(null)
        );

        return Optional.ofNullable(profile);
    }

    public static void setProfileProvider(@NotNull ProfileProvider provider) {
        if (Exceptions.throwOrLogIf(
                PROFILE_PROVIDER != null,
                Origin.ELEGANTIA,
                IllegalStateException::new,
                "{} instance is already set!",
                ProfileProvider.class.getName()
        )) return;

        PROFILE_PROVIDER = provider;
    }

    public static @NotNull EnvironmentType environment() {
        return ENVIRONMENT.unwrap("failed to find out the environment for the {} platform", current());
    }

    static {
        ENVIRONMENT = LazySafeInitializer.create(() -> {
            final var defaultValue = Classes.anyExists(
                    "net.minecraft.client.Minecraft",
                    "org.lwjgl.glfw.GLFW",
                    "net.fabricmc.loader.impl.game.minecraft.MinecraftGameProvider"
            ) ? EnvironmentType.CLIENT : EnvironmentType.SERVER;

            return switch (current()) {
                case FABRIC, QUILT -> ClassContainer.load("net.fabricmc.loader.api.FabricLoader")
                        .method("getInstance")
                        .containedStaticCall()
                        .methodCaller("getEnvironmentType")
                        .call()
                        .map(Object::toString)
                        .map(value -> value.equals("CLIENT") ? EnvironmentType.CLIENT : EnvironmentType.SERVER)
                        .orElse(defaultValue);

                case NEOFORGE -> ClassContainer.load("net.neoforged.fml.loading.FMLEnvironment")
                        .field("dist")
                        .containedStaticRead()
                        .methodCaller("isClient")
                        .call()
                        .map(Boolean.class::cast)
                        .map(value -> value ? EnvironmentType.CLIENT : EnvironmentType.SERVER)
                        .orElse(defaultValue);

                case FORGE -> ClassContainer.load("net.minecraftforge.fml.loading.FMLEnvironment")
                        .field("dist")
                        .containedStaticRead()
                        .methodCaller("isClient")
                        .call()
                        .map(Boolean.class::cast)
                        .map(value -> value ? EnvironmentType.CLIENT : EnvironmentType.SERVER)
                        .orElse(defaultValue);

                default -> defaultValue;
            };
        });

        LOADER = LazySafeInitializer.create(() -> {
            if (Classes.exists("org.quiltmc.loader.api.QuiltLoader")) return QUILT;
            if (Classes.exists("net.fabricmc.loader.api.FabricLoader")) return FABRIC;
            if (Classes.exists("net.neoforged.neoforge.common.NeoForge")) return NEOFORGE;
            if (Classes.exists("net.minecraftforge.common.MinecraftForge")) return FORGE;
            return VANILLA;
        });
    }
}
