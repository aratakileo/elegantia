package io.github.aratakileo.elegantia.common.environment;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@ApiStatus.Internal
public interface ProfileProvider {
    @NotNull Optional<ModProfile> modOptional(@NotNull String id);
}
