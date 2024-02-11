package io.github.aratakileo.elegantia.math;

import org.jetbrains.annotations.NotNull;

public interface Vector2iInterface {
    int x();
    int y();

    int get(int index);

    @NotNull Vector2iInterface sub(int x, int y);
    @NotNull Vector2iInterface sub(@NotNull Vector2iInterface vector2iInterface);
    @NotNull Vector2iInterface sub(@NotNull org.joml.Vector2ic vector2ic);

    @NotNull Vector2iInterface add(int x, int y);
    @NotNull Vector2iInterface add(@NotNull Vector2iInterface vector2iInterface);
    @NotNull Vector2iInterface add(@NotNull org.joml.Vector2ic vector2ic);

    @NotNull Vector2iInterface copy();
}
