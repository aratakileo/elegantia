package io.github.aratakileo.elegantia.math;

import org.jetbrains.annotations.NotNull;

public interface Vector2dInterface {
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
    @NotNull Vector2dInterface sub(@NotNull Vector2dInterface vector2dInterface);
    @NotNull Vector2dInterface sub(@NotNull org.joml.Vector2ic vector2ic);
    @NotNull Vector2dInterface sub(@NotNull org.joml.Vector2dc vector2dc);
    @NotNull Vector2dInterface sub(@NotNull org.joml.Vector2fc vector2fc);

    @NotNull Vector2dInterface add(double value);
    @NotNull Vector2dInterface add(double x, double y);
    @NotNull Vector2dInterface add(@NotNull Vector2iInterface vector2iInterface);
    @NotNull Vector2dInterface add(@NotNull Vector2dInterface vector2dInterface);
    @NotNull Vector2dInterface add(@NotNull org.joml.Vector2ic vector2ic);
    @NotNull Vector2dInterface add(@NotNull org.joml.Vector2dc vector2dc);
    @NotNull Vector2dInterface add(@NotNull org.joml.Vector2fc vector2fc);

    @NotNull Vector2dInterface mul(double value);
    @NotNull Vector2dInterface mul(double x, double y);
    @NotNull Vector2dInterface mul(@NotNull Vector2iInterface vector2iInterface);
    @NotNull Vector2dInterface mul(@NotNull Vector2dInterface vector2dInterface);
    @NotNull Vector2dInterface mul(@NotNull org.joml.Vector2ic vector2ic);
    @NotNull Vector2dInterface mul(@NotNull org.joml.Vector2dc vector2dc);
    @NotNull Vector2dInterface mul(@NotNull org.joml.Vector2fc vector2fc);

    @NotNull Vector2dInterface copy();
}
