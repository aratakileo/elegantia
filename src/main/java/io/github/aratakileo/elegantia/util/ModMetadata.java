package io.github.aratakileo.elegantia.util;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public interface ModMetadata {
    @NotNull String getVersion();
    @NotNull String getName();

    static @NotNull Optional<ModMetadata> get(@NotNull String modId) {
        final var modMetadata = FabricLoader.getInstance()
                .getModContainer(modId)
                .map(ModContainer::getMetadata)
                .orElse(null);

        if (Objects.isNull(modMetadata))
            return Optional.empty();

        return Optional.of(new ModMetadata() {
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
}
