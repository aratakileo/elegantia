package io.github.aratakileo.elegantia.math;

import org.jetbrains.annotations.NotNull;

public class Vector2ic implements Vector2iInterface {
    public final int x, y;

    public Vector2ic(int @NotNull [] vector) {
        this.x = vector[0];
        this.y = vector[1];
    }

    public Vector2ic(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int x() {
        return x;
    }

    @Override
    public int y() {
        return y;
    }

    @Override
    public int get(int index) {
        return switch (index) {
            case 0 -> x;
            case 1 -> y;
            default -> throw new IllegalArgumentException();
        };
    }

    @Override
    public @NotNull Vector2ic sub(int x, int y) {
        return new Vector2ic(this.x - x, this.y - y);
    }

    @Override
    public @NotNull Vector2ic sub(@NotNull Vector2iInterface vector2iInterface) {
        return new Vector2ic(x - vector2iInterface.x(), y - vector2iInterface.y());
    }

    @Override
    public @NotNull Vector2ic sub(@NotNull org.joml.Vector2ic vector2ic) {
        return new Vector2ic(x - vector2ic.x(), y - vector2ic.y());
    }

    @Override
    public @NotNull Vector2ic add(int x, int y) {
        return new Vector2ic(this.x + x, this.y + y);
    }

    @Override
    public @NotNull Vector2ic add(@NotNull Vector2iInterface vector2iInterface) {
        return new Vector2ic(x + vector2iInterface.x(), y + vector2iInterface.y());
    }

    @Override
    public @NotNull Vector2ic add(@NotNull org.joml.Vector2ic vector2ic) {
        return new Vector2ic(x + vector2ic.x(), y + vector2ic.y());
    }

    @Override
    public @NotNull Vector2ic copy() {
        return new Vector2ic(x, y);
    }

    public @NotNull Vector2i copyAsMutable() {
        return new Vector2i(x, y);
    }

    @Override
    public String toString() {
        return "Vector2ic{%d, %d}".formatted(x, y);
    }
}
