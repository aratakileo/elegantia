package io.github.aratakileo.elegantia.math;

import org.jetbrains.annotations.NotNull;

public class Vector2i implements Vector2iInterface {
    public int x, y;

    public Vector2i(int @NotNull [] vector) {
        this.x = vector[0];
        this.y = vector[1];
    }

    public Vector2i(int x, int y) {
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

    public @NotNull Vector2i set(int index, int value) {
        switch (index) {
            case 0 -> this.x = value;
            case 1 -> this.y = value;
            default -> throw new IllegalArgumentException();
        }

        return this;
    }

    @Override
    public @NotNull Vector2i sub(int x, int y) {
        this.x -= x;
        this.y -= y;
        return this;
    }

    @Override
    public @NotNull Vector2i sub(@NotNull Vector2iInterface vector2iInterface) {
        return sub(vector2iInterface.x(), vector2iInterface.y());
    }

    @Override
    public @NotNull Vector2i sub(org.joml.@NotNull Vector2ic vector2ic) {
        return sub(vector2ic.x(), vector2ic.y());
    }

    @Override
    public @NotNull Vector2i add(int x, int y) {
        this.x += x;
        this.y += y;
        return this;
    }

    @Override
    public @NotNull Vector2i add(@NotNull Vector2iInterface vector2iInterface) {
        return add(vector2iInterface.x(), vector2iInterface.y());
    }

    @Override
    public @NotNull Vector2i add(org.joml.@NotNull Vector2ic vector2ic) {
        return add(vector2ic.x(), vector2ic.y());
    }

    @Override
    public @NotNull Vector2i copy() {
        return new Vector2i(x, y);
    }

    public @NotNull Vector2ic copyAsImmutable() {
        return new Vector2ic(x, y);
    }

    @Override
    public String toString() {
        return "Vector2i{%d, %d}".formatted(x, y);
    }
}
