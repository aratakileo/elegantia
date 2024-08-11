package io.github.aratakileo.elegantia.core.math;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public interface Vector2fInterface extends VectorInterface<Vector2fInterface> {
    float x();
    float y();

    default float get(int index) {
        return switch (index) {
            case 0 -> x();
            case 1 -> y();
            default -> throw new IllegalArgumentException();
        };
    }

    default double length() {
        return Math.sqrt(x() * x() + y() * y());
    }

    @NotNull Vector2fInterface sub(float value);
    @NotNull Vector2fInterface sub(float x, float y);
    @NotNull Vector2fInterface sub(@NotNull Vector2iInterface vector2iInterface);
    @NotNull Vector2fInterface sub(@NotNull org.joml.Vector2ic vector2ic);
    @NotNull Vector2fInterface sub(@NotNull org.joml.Vector2fc vector2fc);

    @NotNull Vector2fInterface add(float value);
    @NotNull Vector2fInterface add(float x, float y);
    @NotNull Vector2fInterface add(@NotNull Vector2iInterface vector2iInterface);
    @NotNull Vector2fInterface add(@NotNull org.joml.Vector2ic vector2ic);
    @NotNull Vector2fInterface add(@NotNull org.joml.Vector2fc vector2fc);

    @NotNull Vector2fInterface mul(float value);
    @NotNull Vector2fInterface mul(float x, float y);
    @NotNull Vector2fInterface mul(@NotNull Vector2iInterface vector2iInterface);
    @NotNull Vector2fInterface mul(@NotNull org.joml.Vector2ic vector2ic);
    @NotNull Vector2fInterface mul(@NotNull org.joml.Vector2fc vector2fc);

    @NotNull Vector2fInterface div(float value);
    @NotNull Vector2fInterface div(float x, float y);
    @NotNull Vector2fInterface div(@NotNull Vector2iInterface vector2iInterface);
    @NotNull Vector2fInterface div(@NotNull org.joml.Vector2ic vector2ic);
    @NotNull Vector2fInterface div(@NotNull org.joml.Vector2fc vector2fc);

    @NotNull Vector2fInterface floor();
    @NotNull Vector2fInterface ceil();
    @NotNull Vector2fInterface round();

    @NotNull Vector2iInterface asVec2i();
    @NotNull Vector2dInterface asVec2d();
    default @NotNull Vector3f asVector3f(float z) {
        return new Vector3f(x(), y(), z);
    }

    default boolean equals(float x, float y) {
        return Float.floatToIntBits(x) == Float.floatToIntBits(x())
                && Float.floatToIntBits(y) == Float.floatToIntBits(y());
    }

    default boolean equals(@NotNull Vector2fInterface vec2f) {
        return x() == vec2f.x() && y() == vec2f.y();
    }
}
