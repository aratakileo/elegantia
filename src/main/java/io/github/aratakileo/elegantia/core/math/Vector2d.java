package io.github.aratakileo.elegantia.core.math;

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
    public @NotNull Vector2d neg() {
        x = -x;
        y = -y;
        return this;
    }

    @Override
    public @NotNull Vector2d abs() {
        x = Math.abs(x);
        y = Math.abs(y);
        return this;
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
    public @NotNull Vector2d sub(double value) {
        return sub(value, value);
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
    public @NotNull Vector2d add(double value) {
        return add(value, value);
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
    public @NotNull Vector2d mul(double value) {
        return mul(value, value);
    }

    @Override
    public @NotNull Vector2d mul(double x, double y) {
        this.x *= x;
        this.y *= y;
        return this;
    }

    @Override
    public @NotNull Vector2d mul(@NotNull Vector2iInterface vector2iInterface) {
        return mul(vector2iInterface.x(), vector2iInterface.y());
    }

    @Override
    public @NotNull Vector2d mul(@NotNull Vector2dInterface vector2dInterface) {
        return mul(vector2dInterface.x(), vector2dInterface.y());
    }

    @Override
    public @NotNull Vector2d mul(@NotNull org.joml.Vector2ic vector2ic) {
        return mul(vector2ic.x(), vector2ic.y());
    }

    @Override
    public @NotNull Vector2d mul(@NotNull org.joml.Vector2dc vector2dc) {
        return mul(vector2dc.x(), vector2dc.y());
    }

    @Override
    public @NotNull Vector2d mul(@NotNull org.joml.Vector2fc vector2fc) {
        return mul(vector2fc.x(), vector2fc.y());
    }

    @Override
    public @NotNull Vector2d div(double value) {
        return div(value, value);
    }

    @Override
    public @NotNull Vector2d div(double x, double y) {
        this.x /= x;
        this.y /= y;

        return this;
    }

    @Override
    public @NotNull Vector2d div(@NotNull Vector2iInterface vector2iInterface) {
        return div(vector2iInterface.x(), vector2iInterface.y());
    }

    @Override
    public @NotNull Vector2d div(@NotNull Vector2dInterface vector2dInterface) {
        return div(vector2dInterface.x(), vector2dInterface.y());
    }

    @Override
    public @NotNull Vector2d div(@NotNull org.joml.Vector2ic vector2ic) {
        return div(vector2ic.x(), vector2ic.y());
    }

    @Override
    public @NotNull Vector2d div(@NotNull org.joml.Vector2dc vector2dc) {
        return div(vector2dc.x(), vector2dc.y());
    }

    @Override
    public @NotNull Vector2d div(@NotNull org.joml.Vector2fc vector2fc) {
        return div(vector2fc.x(), vector2fc.y());
    }

    @Override
    public @NotNull Vector2d floor() {
        x = Math.floor(x);
        y = Math.floor(y);
        return this;
    }

    @Override
    public @NotNull Vector2d ceil() {
        x = Math.ceil(x);
        y = Math.ceil(y);
        return this;
    }

    @Override
    public @NotNull Vector2d round() {
        x = Math.round(x);
        y = Math.round(y);
        return this;
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

    public static @NotNull Vector2d of(@NotNull Vector2iInterface vector2iInterface) {
        return new Vector2d(vector2iInterface.x(), vector2iInterface.y());
    }

    public static @NotNull Vector2d of(@NotNull Vector2dInterface vector2dInterface) {
        return new Vector2d(vector2dInterface.x(), vector2dInterface.y());
    }

    public static @NotNull Vector2d of(@NotNull org.joml.Vector2ic vector2ic) {
        return new Vector2d(vector2ic.x(), vector2ic.y());
    }

    public static @NotNull Vector2d of(@NotNull org.joml.Vector2dc vector2dc) {
        return new Vector2d(vector2dc.x(), vector2dc.y());
    }

    public static @NotNull Vector2d of(@NotNull org.joml.Vector2fc vector2fc) {
        return new Vector2d(vector2fc.x(), vector2fc.y());
    }

    public static @NotNull Vector2d zero() {
        return new Vector2d(0, 0);
    }
}
