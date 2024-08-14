package io.github.aratakileo.elegantia.util.type;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Allows to delay initialization of the value exactly until the first attempt to get the value is made
 * @param <T> any type
 */
public class InitOnGet<T> {
    private @Nullable Supplier<T> getter = null;
    private @Nullable T value = null;

    private InitOnGet(@NotNull T value) {
        this.value = value;
    }

    private InitOnGet(@NotNull Supplier<T> getter) {
        this.getter = getter;
    }

    public @NotNull T get() {
        if (value == null) {
            value = Objects.requireNonNull(getter).get();
            getter = null;
        }

        return value;
    }

    public static <T> @NotNull InitOnGet<T> of(@NotNull T value) {
        return new InitOnGet<>(value);
    }

    public static <T> @NotNull InitOnGet<T> of(@NotNull Supplier<T> getter) {
        return new InitOnGet<>(getter);
    }
}
