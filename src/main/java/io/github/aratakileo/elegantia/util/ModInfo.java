package io.github.aratakileo.elegantia.util;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

public interface ModInfo {
    @NotNull String getVersion();
    @NotNull String getName();
    @NotNull Optional<String> getSourcesUrl();

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

            @Override
            public @NotNull Optional<String> getSourcesUrl() {
                return modMetadata.getContact().get("sources");
            }
        });
    }

    static @NotNull Optional<String> getName(@NotNull String modId) {
        return get(modId).map(ModInfo::getName);
    }

    static @NotNull Optional<String> getVersion(@NotNull String modId) {
        return get(modId).map(ModInfo::getVersion);
    }

    static @NotNull Optional<String> getSourcesUrl(@NotNull String modId) {
        return get(modId).flatMap(ModInfo::getSourcesUrl);
    }

    static boolean isModLoaded(@NotNull String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }
}
