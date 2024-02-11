package io.github.aratakileo.elegantia.math;

import org.jetbrains.annotations.NotNull;

public class Vector2d implements Vector2dInterface {
    public double x, y;

    public Vector2d(double @NotNull [] vector) {
        this.x = vector[0];
        this.y = vector[1];
    }

    public Vector2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public double x() {
        return x;
    }

    @Override
    public double y() {
        return y;
    }

    @Override
    public double get(int index) {
        return switch (index) {
            case 0 -> x;
            case 1 -> y;
            default -> throw new IllegalArgumentException();
        };
    }

    public @NotNull Vector2d set(int index, double value) {
        switch (index) {
            case 0 -> this.x = value;
            case 1 -> this.y = value;
            default -> throw new IllegalArgumentException();
        }

        return this;
    }

    @Override
    public @NotNull Vector2d sub(double x, double y) {
        this.x -= x;
        this.y -= y;
        return this;
    }

    @Override
    public @NotNull Vector2d sub(@NotNull Vector2iInterface vector2iInterface) {
        return sub(vector2iInterface.x(), vector2iInterface.y());
    }

    @Override
    public @NotNull Vector2d sub(@NotNull Vector2dInterface vector2dInterface) {
        return sub(vector2dInterface.x(), vector2dInterface.y());
    }

    @Override
    public @NotNull Vector2d sub(@NotNull org.joml.Vector2ic vector2ic) {
        return sub(vector2ic.x(), vector2ic.y());
    }

    @Override
    public @NotNull Vector2d sub(@NotNull org.joml.Vector2dc vector2dc) {
        return sub(vector2dc.x(), vector2dc.y());
    }

    @Override
    public @NotNull Vector2d sub(@NotNull org.joml.Vector2fc vector2ic) {
        return sub(vector2ic.x(), vector2ic.y());
    }

    @Override
    public @NotNull Vector2d add(double x, double y) {
        this.x += x;
        this.y += y;
        return this;
    }

    @Override
    public @NotNull Vector2d add(@NotNull Vector2iInterface vector2iInterface) {
        return add(vector2iInterface.x(), vector2iInterface.y());
    }

    @Override
    public @NotNull Vector2d add(@NotNull Vector2dInterface vector2dInterface) {
        return add(vector2dInterface.x(), vector2dInterface.y());
    }

    @Override
    public @NotNull Vector2d add(@NotNull org.joml.Vector2ic vector2ic) {
        return add(vector2ic.x(), vector2ic.y());
    }

    @Override
    public @NotNull Vector2d add(@NotNull org.joml.Vector2dc vector2dc) {
        return add(vector2dc.x(), vector2dc.y());
    }

    @Override
    public @NotNull Vector2d add(@NotNull org.joml.Vector2fc vector2ic) {
        return add(vector2ic.x(), vector2ic.y());
    }

    @Override
    public @NotNull Vector2d copy() {
        return new Vector2d(x, y);
    }

    public @NotNull Vector2dc copyAsImmutable() {
        return new Vector2dc(x, y);
    }

    @Override
    public String toString() {
        return "Vector2d{%s, %s}".formatted(x, y);
    }
}
