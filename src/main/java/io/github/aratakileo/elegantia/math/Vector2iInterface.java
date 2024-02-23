package io.github.aratakileo.elegantia.math;

import org.jetbrains.annotations.NotNull;

public interface Vector2iInterface extends VectorInterface<Vector2iInterface> {
    int x();
    int y();

    default int get(int index) {
        return switch (index) {
            case 0 -> x();
            case 1 -> y();
            default -> throw new IllegalArgumentException();
        };
    }

    default double length() {
        return Math.sqrt(x() * x() + y() * y());
    }

    @NotNull Vector2iInterface sub(int value);
    @NotNull Vector2iInterface sub(int x, int y);
    @NotNull Vector2iInterface sub(@NotNull org.joml.Vector2ic vector2ic);

    @NotNull Vector2iInterface add(int value);
    @NotNull Vector2iInterface add(int x, int y);
    @NotNull Vector2iInterface add(@NotNull org.joml.Vector2ic vector2ic);

    @NotNull Vector2iInterface mul(int value);
    @NotNull Vector2iInterface mul(int x, int y);
    @NotNull Vector2iInterface mul(@NotNull org.joml.Vector2ic vector2ic);

    @NotNull Vector2iInterface div(int value);
    @NotNull Vector2iInterface div(int x, int y);
    @NotNull Vector2iInterface div(@NotNull org.joml.Vector2ic vector2ic);

    default boolean equals(int x, int y) {
        return x() == x && y() == y;
    }
}
