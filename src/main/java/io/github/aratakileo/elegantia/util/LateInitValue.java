package io.github.aratakileo.elegantia.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public class LateInitValue<T> {
    private @Nullable T value = null;

    public boolean isInited() {
        return Objects.nonNull(value);
    }

    public void set(@NotNull T value) {
        if (Objects.nonNull(this.value)) return;

        this.value = value;
    }

    public @NotNull Optional<T> get() {
        return Optional.ofNullable(value);
    }
}
