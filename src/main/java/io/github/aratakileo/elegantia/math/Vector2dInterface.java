package io.github.aratakileo.elegantia.math;

import org.jetbrains.annotations.NotNull;

public interface Vector2dInterface {
    double x();
    double y();

    double get(int index);

    @NotNull Vector2dInterface sub(double x, double y);
    @NotNull Vector2dInterface sub(@NotNull Vector2iInterface vector2iInterface);
    @NotNull Vector2dInterface sub(@NotNull Vector2dInterface vector2dInterface);
    @NotNull Vector2dInterface sub(@NotNull org.joml.Vector2ic vector2ic);
    @NotNull Vector2dInterface sub(@NotNull org.joml.Vector2dc vector2dc);
    @NotNull Vector2dInterface sub(@NotNull org.joml.Vector2fc vector2fc);

    @NotNull Vector2dInterface add(double x, double y);
    @NotNull Vector2dInterface add(@NotNull Vector2iInterface vector2iInterface);
    @NotNull Vector2dInterface add(@NotNull Vector2dInterface vector2dInterface);
    @NotNull Vector2dInterface add(@NotNull org.joml.Vector2ic vector2ic);
    @NotNull Vector2dInterface add(@NotNull org.joml.Vector2dc vector2dc);
    @NotNull Vector2dInterface add(@NotNull org.joml.Vector2fc vector2fc);

    @NotNull Vector2dInterface copy();
}
