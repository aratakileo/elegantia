package io.github.aratakileo.elegantia.util.math;

import org.jetbrains.annotations.NotNull;

public interface Vector2dInterface extends VectorInterface<Vector2dInterface> {
    double x();
    double y();

    default double get(int index) {
        return switch (index) {
            case 0 -> x();
            case 1 -> y();
            default -> throw new IllegalArgumentException();
        };
    }

    default double length() {
        return Math.sqrt(x() * x() + y() * y());
    }

    @NotNull Vector2dInterface sub(double value);
    @NotNull Vector2dInterface sub(double x, double y);
    @NotNull Vector2dInterface sub(@NotNull Vector2iInterface vector2iInterface);
    @NotNull Vector2dInterface sub(@NotNull org.joml.Vector2ic vector2ic);
    @NotNull Vector2dInterface sub(@NotNull org.joml.Vector2dc vector2dc);
    @NotNull Vector2dInterface sub(@NotNull org.joml.Vector2fc vector2fc);

    @NotNull Vector2dInterface add(double value);
    @NotNull Vector2dInterface add(double x, double y);
    @NotNull Vector2dInterface add(@NotNull Vector2iInterface vector2iInterface);
    @NotNull Vector2dInterface add(@NotNull org.joml.Vector2ic vector2ic);
    @NotNull Vector2dInterface add(@NotNull org.joml.Vector2dc vector2dc);
    @NotNull Vector2dInterface add(@NotNull org.joml.Vector2fc vector2fc);

    @NotNull Vector2dInterface mul(double value);
    @NotNull Vector2dInterface mul(double x, double y);
    @NotNull Vector2dInterface mul(@NotNull Vector2iInterface vector2iInterface);
    @NotNull Vector2dInterface mul(@NotNull org.joml.Vector2ic vector2ic);
    @NotNull Vector2dInterface mul(@NotNull org.joml.Vector2dc vector2dc);
    @NotNull Vector2dInterface mul(@NotNull org.joml.Vector2fc vector2fc);

    @NotNull Vector2dInterface div(double value);
    @NotNull Vector2dInterface div(double x, double y);
    @NotNull Vector2dInterface div(@NotNull Vector2iInterface vector2iInterface);
    @NotNull Vector2dInterface div(@NotNull org.joml.Vector2ic vector2ic);
    @NotNull Vector2dInterface div(@NotNull org.joml.Vector2dc vector2dc);
    @NotNull Vector2dInterface div(@NotNull org.joml.Vector2fc vector2fc);

    @NotNull Vector2dInterface floor();
    @NotNull Vector2dInterface ceil();
    @NotNull Vector2dInterface round();

    default boolean equals(int x, int y) {
        return equals((double) x, y);
    }

    default boolean equals(double x, double y) {
        return Double.doubleToLongBits(x) == Double.doubleToLongBits(x())
                && Double.doubleToLongBits(y) == Double.doubleToLongBits(y());
    }

    default boolean equals(float x, float y) {
        return equals((double) x, y);
    }
}
