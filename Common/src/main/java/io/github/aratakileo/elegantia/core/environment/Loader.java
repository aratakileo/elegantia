package io.github.aratakileo.elegantia.core.environment;

import io.github.aratakileo.elegantia.core.Result;
import io.github.aratakileo.elegantia.util.Classes;
import io.github.aratakileo.elegantia.util.Exceptions;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public enum Loader {
    FABRIC("Fabric"),
    QUILT("Quilt"),
    FORGE("Forge"),
    NEOFORGE("NeoForge"),
    VANILLA("Minecraft");

    private static volatile Loader RESOLVED = null;

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
        if (RESOLVED != null) return RESOLVED;

        if (Classes.exists("org.quiltmc.loader.api.QuiltLoader")) return (RESOLVED = QUILT);
        if (Classes.exists("net.fabricmc.loader.api.FabricLoader")) return (RESOLVED = FABRIC);
        if (Classes.exists("net.neoforged.neoforge.common.NeoForge")) return (RESOLVED = NEOFORGE);
        if (Classes.exists("net.minecraftforge.common.MinecraftForge")) return (RESOLVED = FORGE);

        return (RESOLVED = VANILLA);
    }

    public static boolean currentDefined() {
        return RESOLVED != null;
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
}
