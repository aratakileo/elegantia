package io.github.aratakileo.elegantia.math;

import org.jetbrains.annotations.NotNull;

public class Vector2dc implements Vector2dInterface {
    public final double x, y;

    public Vector2dc(double @NotNull [] vector) {
        this.x = vector[0];
        this.y = vector[1];
    }

    public Vector2dc(double x, double y) {
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

    @Override
    public @NotNull Vector2dc sub(double x, double y) {
        return new Vector2dc(this.x - x, this.y - y);
    }

    @Override
    public @NotNull Vector2dc sub(@NotNull Vector2iInterface vector2iInterface) {
        return new Vector2dc(x - (double) vector2iInterface.x(), y - (double) vector2iInterface.y());
    }

    @Override
    public @NotNull Vector2dc sub(@NotNull Vector2dInterface vector2dInterface) {
        return new Vector2dc(x - vector2dInterface.x(), y - vector2dInterface.y());
    }

    @Override
    public @NotNull Vector2dc sub(@NotNull org.joml.Vector2ic vector2ic) {
        return new Vector2dc(x - (double) vector2ic.x(), y - (double) vector2ic.y());
    }

    @Override
    public @NotNull Vector2dc sub(@NotNull org.joml.Vector2dc vector2dc) {
        return new Vector2dc(x - vector2dc.x(), y - vector2dc.y());
    }

    @Override
    public @NotNull Vector2dc sub(@NotNull org.joml.Vector2fc vector2fc) {
        return new Vector2dc(x - (double) vector2fc.x(), y - (double) vector2fc.y());
    }

    @Override
    public @NotNull Vector2dc add(double x, double y) {
        return new Vector2dc(this.x + x, this.y + y);
    }

    @Override
    public @NotNull Vector2dc add(@NotNull Vector2iInterface vector2iInterface) {
        return new Vector2dc(x + (double) vector2iInterface.x(), y + (double) vector2iInterface.y());
    }

    @Override
    public @NotNull Vector2dc add(@NotNull Vector2dInterface vector2dInterface) {
        return new Vector2dc(x + vector2dInterface.x(), y + vector2dInterface.y());
    }

    @Override
    public @NotNull Vector2dc add(@NotNull org.joml.Vector2ic vector2ic) {
        return new Vector2dc(x + (double) vector2ic.x(), y + (double) vector2ic.y());
    }

    @Override
    public @NotNull Vector2dc add(@NotNull org.joml.Vector2dc vector2dc) {
        return new Vector2dc(x + vector2dc.x(), y + vector2dc.y());
    }

    @Override
    public @NotNull Vector2dc add(@NotNull org.joml.Vector2fc vector2fc) {
        return new Vector2dc(x + (double) vector2fc.x(), y + (double) vector2fc.y());
    }

    @Override
    public @NotNull Vector2dc copy() {
        return new Vector2dc(x, y);
    }

    public @NotNull Vector2d copyAsMutable() {
        return new Vector2d(x, y);
    }

    @Override
    public String toString() {
        return "Vector2dc{%s, %s}".formatted(x, y);
    }
}
