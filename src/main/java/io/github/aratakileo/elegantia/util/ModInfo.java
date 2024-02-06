package io.github.aratakileo.elegantia.util;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

public interface ModInfo {
    @NotNull String getVersion();
    @NotNull String getName();

    static @NotNull Optional<ModInfo> get(@NotNull String modId) {
        final var modMetadata = FabricLoader.getInstance()
                .getModContainer(modId)
                .map(ModContainer::getMetadata)
                .orElse(null);

        if (Objects.isNull(modMetadata))
            return Optional.empty();

        return Optional.of(new ModInfo() {
            @Override
            public @NotNull String getVersion() {
                return modMetadata.getVersion().getFriendlyString();
            }

            @Override
            public @NotNull String getName() {
                return modMetadata.getName();
            }
        });
    }

    static @NotNull Optional<String> getName(@NotNull String modId) {
        return get(modId).map(ModInfo::getName);
    }

    static @NotNull Optional<String> getVersion(@NotNull String modId) {
        return get(modId).map(ModInfo::getVersion);
    }

    static boolean isModLoaded(@NotNull String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }
}
