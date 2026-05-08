package io.github.aratakileo.elegantia.util.type;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class Either<Left, Right> {
    public final @Nullable Left leftValue;
    public final @Nullable Right rightValue;

    private Either(@Nullable Left leftValue, @Nullable Right rightValue) {
        this.leftValue = leftValue;
        this.rightValue = rightValue;
    }

    public boolean isLeft() {
        return leftValue != null;
    }

    public boolean isRight() {
        return rightValue != null;
    }

    public <T> boolean is(@NotNull Class<? extends T> type) {
        return leftValue != null && leftValue.getClass() == type
                || rightValue != null && rightValue.getClass() == type;
    }

    @SuppressWarnings("unchecked")
    public <T> @NotNull Optional<T> get(@NotNull Class<? extends T> type) {
        if (leftValue != null && leftValue.getClass() == type) return Optional.of((T) leftValue);
        if (rightValue != null && rightValue.getClass() == type) return Optional.of((T) rightValue);

        return Optional.empty();
    }

    public Optional<Left> left() {
        return Optional.ofNullable(leftValue);
    }

    public Optional<Right> right() {
        return Optional.ofNullable(rightValue);
    }

    public Either<Left, Right> ifLeft(@NotNull Consumer<@NotNull Left> consumer) {
        left().ifPresent(consumer);
        return this;
    }

    public Either<Left, Right> ifRight(@NotNull Consumer<@NotNull Right> consumer) {
        right().ifPresent(consumer);
        return this;
    }

    public Either<Right, Left> swap() {
        return new Either<>(this.rightValue, this.leftValue);
    }

    @SuppressWarnings("unchecked")
    public <T> T map(@NotNull Function<Left, ? extends T> leftMapper, @NotNull Function<Right, ? extends T> rightMapper) {
        return (T) (isLeft() ? left().map(leftMapper) : right().map(rightMapper));
    }

    public com.mojang.datafixers.util.Either<Left, Right> toMojangEither() {
        return map(com.mojang.datafixers.util.Either::left, com.mojang.datafixers.util.Either::right);
    }

    public static <Left, Right> @NotNull Either<Left, Right> ofLeft(Left value) {
        return new Either<>(value, null);
    }

    public static <Left, Right> @NotNull Either<Left, Right> ofRight(Right value) {
        return new Either<>(null, value);
    }

    public static <Left, Right> @NotNull Either<Left, Right> of(
            Left leftValue,
            @NotNull Class<? extends Right> rightType
    ) {
        return ofLeft(leftValue);
    }

    public static <Left, Right> @NotNull Either<Left, Right> of(
            @NotNull Class<? extends Left> leftType,
            Right rightValue
    ) {
        return ofRight(rightValue);
    }

    public static <Left, Right> @NotNull Either<Left, Right> of(
            com.mojang.datafixers.util.Either<Left, Right> mojangEither
    ) {
        return mojangEither.map(Either::ofLeft, Either::ofRight);
    }

    @Override
    public @NotNull String toString() {
        return isLeft() ? Objects.requireNonNull(leftValue).toString() : Objects.requireNonNull(rightValue).toString();
    }
}
