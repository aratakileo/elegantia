package io.github.aratakileo.elegantia.core.math;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

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

    @NotNull Vector2iInterface asVec2i();
    @NotNull Vector2fInterface asVec2f();
    default @NotNull Vector3d asVector3d(double z) {
        return new Vector3d(x(), y(), z);
    }

    default boolean equals(double x, double y) {
        return Double.doubleToLongBits(x) == Double.doubleToLongBits(x())
                && Double.doubleToLongBits(y) == Double.doubleToLongBits(y());
    }

    default boolean equals(@NotNull Vector2dInterface vec2d) {
        return x() == vec2d.x() && y() == vec2d.y();
    }
}
