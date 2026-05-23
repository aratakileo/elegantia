package io.github.aratakileo.elegantia.core.environment;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface ProfileProvider {
    @NotNull Optional<ModProfile> modOptional(@NotNull String id);
}
