package io.github.aratakileo.elegantia.core.math;

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

    public @NotNull Vector2i set(int index, int value) {
        switch (index) {
            case 0 -> this.x = value;
            case 1 -> this.y = value;
            default -> throw new IllegalArgumentException();
        }

        return this;
    }

    @Override
    public @NotNull Vector2i neg() {
        x = -x;
        y = -y;
        return this;
    }

    @Override
    public @NotNull Vector2i abs() {
        x = Math.abs(x);
        y = Math.abs(y);
        return this;
    }

    @Override
    public @NotNull Vector2i sub(int value) {
        return sub(value, value);
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
    public @NotNull Vector2i sub(@NotNull org.joml.Vector2ic vector2ic) {
        return sub(vector2ic.x(), vector2ic.y());
    }

    @Override
    public @NotNull Vector2i add(int value) {
        return add(value, value);
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
    public @NotNull Vector2i add(@NotNull org.joml.Vector2ic vector2ic) {
        return add(vector2ic.x(), vector2ic.y());
    }

    @Override
    public @NotNull Vector2i mul(int value) {
        return mul(value, value);
    }

    @Override
    public @NotNull Vector2i mul(int x, int y) {
        this.x *= x;
        this.y *= y;
        return this;
    }

    @Override
    public @NotNull Vector2i mul(@NotNull Vector2iInterface vector2iInterface) {
        return mul(vector2iInterface.x(), vector2iInterface.y());
    }

    @Override
    public @NotNull Vector2i mul(@NotNull org.joml.Vector2ic vector2ic) {
        return mul(vector2ic.x(), vector2ic.y());
    }

    @Override
    public @NotNull Vector2i div(int value) {
        return div(value, value);
    }

    @Override
    public @NotNull Vector2i div(int x, int y) {
        this.x /= x;
        this.y /= y;
        return this;
    }

    @Override
    public @NotNull Vector2i div(@NotNull Vector2iInterface vector2iInterface) {
        return div(vector2iInterface.x(), vector2iInterface.y());
    }

    @Override
    public @NotNull Vector2i div(@NotNull org.joml.Vector2ic vector2ic) {
        return div(vector2ic.x(), vector2ic.y());
    }

    @Override
    public @NotNull Vector2f asVec2f() {
        return new Vector2f(x, y);
    }

    @Override
    public @NotNull Vector2d asVec2d() {
        return new Vector2d(x, y);
    }

    @Override
    public @NotNull Vector2i copy() {
        return new Vector2i(x, y);
    }

    public @NotNull Vector2ic asImmutable() {
        return new Vector2ic(x, y);
    }

    @Override
    public String toString() {
        return "Vector2i{%d, %d}".formatted(x, y);
    }

    public static @NotNull Vector2i of(@NotNull Vector2iInterface vector2iInterface) {
        return new Vector2i(vector2iInterface.x(), vector2iInterface.y());
    }

    public static @NotNull Vector2i of(@NotNull org.joml.Vector2ic vector2ic) {
        return new Vector2i(vector2ic.x(), vector2ic.y());
    }

    public static @NotNull Vector2i zero() {
        return new Vector2i(0, 0);
    }
}
