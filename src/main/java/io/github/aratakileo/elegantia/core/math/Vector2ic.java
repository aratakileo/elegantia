package io.github.aratakileo.elegantia.core.math;

import org.jetbrains.annotations.NotNull;

public class Vector2ic implements Vector2iInterface {
    public static final Vector2ic ZERO = new Vector2ic(0, 0);

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
    public @NotNull Vector2ic sub(int value) {
        return sub(value, value);
    }

    @Override
    public @NotNull Vector2ic sub(int x, int y) {
        return new Vector2ic(this.x - x, this.y - y);
    }

    @Override
    public @NotNull Vector2ic neg() {
        return new Vector2ic(-x, -y);
    }

    @Override
    public @NotNull Vector2ic abs() {
        return new Vector2ic(Math.abs(x), Math.abs(y));
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
    public @NotNull Vector2ic add(int value) {
        return sub(value, value);
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
    public @NotNull Vector2ic mul(int value) {
        return new Vector2ic(x * value, y * value);
    }

    @Override
    public @NotNull Vector2ic mul(int x, int y) {
        return new Vector2ic(this.x * x, this.y * y);
    }

    @Override
    public @NotNull Vector2ic mul(@NotNull Vector2iInterface vector2iInterface) {
        return new Vector2ic(x * vector2iInterface.x(), y * vector2iInterface.y());
    }

    @Override
    public @NotNull Vector2ic mul(@NotNull org.joml.Vector2ic vector2ic) {
        return new Vector2ic(x * vector2ic.x(), y * vector2ic.y());
    }

    @Override
    public @NotNull Vector2ic div(int value) {
        return new Vector2ic(x / value, y / value);
    }

    @Override
    public @NotNull Vector2ic div(int x, int y) {
        return new Vector2ic(this.x / x, this.y / y);
    }

    @Override
    public @NotNull Vector2ic div(@NotNull Vector2iInterface vector2iInterface) {
        return new Vector2ic(x / vector2iInterface.x(), y / vector2iInterface.y());
    }

    @Override
    public @NotNull Vector2ic div(@NotNull org.joml.Vector2ic vector2ic) {
        return new Vector2ic(x / vector2ic.x(), y / vector2ic.y());
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

    public static @NotNull Vector2ic of(@NotNull Vector2iInterface vector2iInterface) {
        return new Vector2ic(vector2iInterface.x(), vector2iInterface.y());
    }

    public static @NotNull Vector2ic of(@NotNull org.joml.Vector2ic vector2ic) {
        return new Vector2ic(vector2ic.x(), vector2ic.y());
    }
}
