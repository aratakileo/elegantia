package io.github.aratakileo.elegantia.util.type;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Allows to delay initialization of the value exactly until the first attempt to get the value is made
 * @param <T> any type
 */
public class InitOnGet<T> {
    private @Nullable Supplier<T> getter = null;
    private @Nullable T value = null;

    private InitOnGet(@Nullable T value) {
        this.value = value;
    }

    private InitOnGet(@NotNull Supplier<T> getter) {
        this.getter = getter;
    }

    public @Nullable T get() {
        if (getter != null) {
            value = Objects.requireNonNull(getter).get();
            getter = null;
        }

        return value;
    }

    public @NotNull T getOrThrow() {
        return Objects.requireNonNull(get());
    }

    public @NotNull Optional<T> getOptional() {
        return Optional.ofNullable(get());
    }

    public static <T> @NotNull InitOnGet<T> of(@Nullable T value) {
        return new InitOnGet<>(value);
    }

    public static <T> @NotNull InitOnGet<T> build(@NotNull Supplier<T> getter) {
        return new InitOnGet<>(getter);
    }

    public static <T, E> @NotNull InitOnGet<T> build(@NotNull E inputValue, @NotNull Function<E, T> builder) {
        return new InitOnGet<>(() -> builder.apply(inputValue));
    }

    public static <T, E> @NotNull InitOnGet<T> buildOn(
            @NotNull InitOnGet<E> inputValue,
            @NotNull Function<E, T> builder
    ) {
        return new InitOnGet<>(() -> builder.apply(inputValue.get()));
    }

    public static <T, E> @NotNull InitOnGet<T> buildOptionalOn(
            @NotNull InitOnGet<E> inputValue,
            @NotNull Function<E, Optional<T>> builder
    ) {
        return new InitOnGet<>(() -> builder.apply(inputValue.get()).orElse(null));
    }

    public static <T, E> @NotNull InitOnGet<T> buildOptional(
            @NotNull E inputValue,
            @NotNull Function<E, Optional<T>> builder
    ) {
        return new InitOnGet<>(() -> builder.apply(inputValue).orElse(null));
    }
}
