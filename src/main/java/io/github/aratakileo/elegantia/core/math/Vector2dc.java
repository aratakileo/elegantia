package io.github.aratakileo.elegantia.core.math;

import org.jetbrains.annotations.NotNull;
import org.joml.Math;

public class Vector2dc implements Vector2dInterface {
    public static final Vector2dc ZERO = new Vector2dc(0, 0);

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
    public @NotNull Vector2dc neg() {
        return new Vector2dc(-x, -y);
    }

    @Override
    public @NotNull Vector2dc abs() {
        return new Vector2dc(Math.abs(x), Math.abs(y));
    }

    @Override
    public @NotNull Vector2dc sub(double value) {
        return new Vector2dc(x - value, y - value);
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
    public @NotNull Vector2dc add(double value) {
        return new Vector2dc(x + value, y + value);
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
    public @NotNull Vector2dc mul(double value) {
        return new Vector2dc(x * value, y * value);
    }

    @Override
    public @NotNull Vector2dc mul(double x, double y) {
        return new Vector2dc(this.x * x, this.y * y);
    }

    @Override
    public @NotNull Vector2dc mul(@NotNull Vector2iInterface vector2iInterface) {
        return new Vector2dc(x * vector2iInterface.x(), y * vector2iInterface.y());
    }

    @Override
    public @NotNull Vector2dc mul(@NotNull Vector2dInterface vector2dInterface) {
        return new Vector2dc(x * vector2dInterface.x(), y * vector2dInterface.y());
    }

    @Override
    public @NotNull Vector2dc mul(@NotNull org.joml.Vector2ic vector2ic) {
        return new Vector2dc(x * vector2ic.x(), y * vector2ic.y());
    }

    @Override
    public @NotNull Vector2dc mul(@NotNull org.joml.Vector2dc vector2dc) {
        return new Vector2dc(x * vector2dc.x(), y * vector2dc.y());
    }

    @Override
    public @NotNull Vector2dc mul(@NotNull org.joml.Vector2fc vector2fc) {
        return new Vector2dc(x * vector2fc.x(), y * vector2fc.y());
    }

    @Override
    public @NotNull Vector2dc div(double value) {
        return new Vector2dc(x / value, y / value);
    }

    @Override
    public @NotNull Vector2dc div(double x, double y) {
        return new Vector2dc(this.x / x, this.y / y);
    }

    @Override
    public @NotNull Vector2dc div(@NotNull Vector2iInterface vector2iInterface) {
        return new Vector2dc(x / vector2iInterface.x(), y / vector2iInterface.y());
    }

    @Override
    public @NotNull Vector2dc div(@NotNull Vector2dInterface vector2dInterface) {
        return new Vector2dc(x / vector2dInterface.x(), y / vector2dInterface.y());
    }

    @Override
    public @NotNull Vector2dc div(@NotNull org.joml.Vector2ic vector2ic) {
        return new Vector2dc(x / vector2ic.x(), y / vector2ic.y());
    }

    @Override
    public @NotNull Vector2dc div(@NotNull org.joml.Vector2dc vector2dc) {
        return new Vector2dc(x / vector2dc.x(), y / vector2dc.y());
    }

    @Override
    public @NotNull Vector2dc div(@NotNull org.joml.Vector2fc vector2fc) {
        return new Vector2dc(x / vector2fc.x(), y / vector2fc.y());
    }

    @Override
    public @NotNull Vector2dc max(@NotNull Vector2dInterface vector2dInterface) {
        return new Vector2dc(Math.max(x, vector2dInterface.x()), Math.max(y, vector2dInterface.y()));
    }

    @Override
    public @NotNull Vector2dc min(@NotNull Vector2dInterface vector2dInterface) {
        return new Vector2dc(Math.min(x, vector2dInterface.x()), Math.min(y, vector2dInterface.y()));
    }

    @Override
    public @NotNull Vector2dc perpendicular() {
        return new Vector2dc(x * -1, y);
    }

    @Override
    public @NotNull Vector2dc normalize() {
        final var invLength = Math.invsqrt(x * x + y * y);

        return new Vector2dc(x * invLength, y * invLength);
    }

    @Override
    public @NotNull Vector2dc floor() {
        return new Vector2dc(Math.floor(x), Math.floor(y));
    }

    @Override
    public @NotNull Vector2dc ceil() {
        return new Vector2dc(Math.floor(x), Math.floor(y));
    }

    @Override
    public @NotNull Vector2dc round() {
        return new Vector2dc(Math.round(x), Math.round(y));
    }

    @Override
    public @NotNull Vector2dc copy() {
        return new Vector2dc(x, y);
    }

    @Override
    public @NotNull Vector2ic asVec2i() {
        return new Vector2ic((int)x, (int)y);
    }

    @Override
    public @NotNull Vector2fc asVec2f() {
        return new Vector2fc((float)x, (float)y);
    }

    public @NotNull Vector2d asMutable() {
        return new Vector2d(x, y);
    }

    @Override
    public String toString() {
        return "Vector2dc{%s, %s}".formatted(x, y);
    }

    public static @NotNull Vector2dc of(@NotNull Vector2iInterface vector2iInterface) {
        return new Vector2dc(vector2iInterface.x(), vector2iInterface.y());
    }

    public static @NotNull Vector2dc of(@NotNull Vector2dInterface vector2dInterface) {
        return new Vector2dc(vector2dInterface.x(), vector2dInterface.y());
    }

    public static @NotNull Vector2dc of(@NotNull org.joml.Vector2ic vector2ic) {
        return new Vector2dc(vector2ic.x(), vector2ic.y());
    }

    public static @NotNull Vector2dc of(@NotNull org.joml.Vector2dc vector2dc) {
        return new Vector2dc(vector2dc.x(), vector2dc.y());
    }

    public static @NotNull Vector2dc of(@NotNull org.joml.Vector2fc vector2fc) {
        return new Vector2dc(vector2fc.x(), vector2fc.y());
    }

    public static @NotNull Vector2dc createXY(double value) {
        return new Vector2dc(value, value);
    }
}
