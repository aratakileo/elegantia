package io.github.aratakileo.elegantia.util.type;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.NoSuchElementException;
import java.util.Optional;

public class LateInitedConst<T> {
    private @Nullable T value = null;

    public boolean isInited() {
        return value != null;
    }

    public void set(@NotNull T value) {
        if (this.value != null) return;

        this.value = value;
    }

    public @NotNull Optional<T> asOptional() {
        return Optional.ofNullable(value);
    }

    public @NotNull T getOrThrow() {
        if (value == null)
            throw new NoSuchElementException("No value present");

        return value;
    }
}
