package io.github.aratakileo.elegantia.core;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface DeltaTimeSupplier {
    long deltaTime();
}
