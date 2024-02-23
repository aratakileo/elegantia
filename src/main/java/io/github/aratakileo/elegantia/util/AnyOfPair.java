package io.github.aratakileo.elegantia.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

public class AnyOfPair<Left, Right> {
    public final @Nullable Left leftValue;
    public final @Nullable Right rightValue;

    private AnyOfPair(@Nullable Left leftValue, @Nullable Right rightValue) {
        this.leftValue = leftValue;
        this.rightValue = rightValue;
    }

    public boolean isLeft() {
        return Objects.nonNull(leftValue);
    }

    public boolean isRight() {
        return Objects.nonNull(rightValue);
    }

    public <T> boolean is(@NotNull Class<? extends T> type) {
        return Objects.nonNull(leftValue) && leftValue.getClass() == type
                || Objects.nonNull(rightValue) && rightValue.getClass() == type;
    }

    public <T> void ifIs(@NotNull Class<? extends T> type, @NotNull Consumer<@NotNull T> consumer) {
        if (!is(type)) return;

        final var value = get(type);

        if (Objects.nonNull(value)) consumer.accept(value);
    }

    @SuppressWarnings("unchecked")
    public <T> @Nullable T get(@NotNull Class<? extends T> type) {
        if (Objects.nonNull(leftValue) && leftValue.getClass() == type) return (T) leftValue;
        if (Objects.nonNull(rightValue) && rightValue.getClass() == type) return (T) rightValue;

        return null;
    }

    public void ifIsLeft(@NotNull Consumer<@NotNull Left> consumer) {
        if (isLeft()) consumer.accept(Objects.requireNonNull(leftValue));
    }

    public void ifIsRight(@NotNull Consumer<@NotNull Right> consumer) {
        if (isRight()) consumer.accept(Objects.requireNonNull(rightValue));
    }

    public static <Left, Right> @NotNull AnyOfPair<Left, Right> ofLeft(Left value) {
        return new AnyOfPair<>(value, null);
    }

    public static <Left, Right> @NotNull AnyOfPair<Left, Right> ofRight(Right value) {
        return new AnyOfPair<>(null, value);
    }

    public static <Left, Right> @NotNull AnyOfPair<Left, Right> of(
            Left leftValue,
            @NotNull Class<? extends Right> rightType
    ) {
        return ofLeft(leftValue);
    }

    public static <Left, Right> @NotNull AnyOfPair<Left, Right> of(
            @NotNull Class<? extends Left> leftType,
            Right rightValue
    ) {
        return ofRight(rightValue);
    }

    @Override
    public @NotNull String toString() {
        return isLeft() ? Objects.requireNonNull(leftValue).toString() : Objects.requireNonNull(rightValue).toString();
    }
}
