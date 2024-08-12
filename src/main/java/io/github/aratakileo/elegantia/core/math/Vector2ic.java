package io.github.aratakileo.elegantia.core.math;

import org.jetbrains.annotations.NotNull;
import org.joml.Math;

import java.awt.geom.Point2D;

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
    public @NotNull Vector2ic max(@NotNull Vector2iInterface vector2iInterface) {
        return new Vector2ic(Math.max(x, vector2iInterface.x()), Math.max(y, vector2iInterface.y()));
    }

    @Override
    public @NotNull Vector2ic min(@NotNull Vector2iInterface vector2iInterface) {
        return new Vector2ic(Math.min(x, vector2iInterface.x()), Math.min(y, vector2iInterface.y()));
    }

    @Override
    public @NotNull Vector2ic perpendicular() {
        return new Vector2ic(x * -1, y);
    }

    @Override
    public @NotNull Vector2ic normalize() {
        final var invLength = Math.invsqrt((double) x * x + y * y);

        return new Vector2ic((int) (x * invLength), (int) (y * invLength));
    }

    @Override
    public @NotNull Vector2fc asVec2f() {
        return new Vector2fc(x, y);
    }

    @Override
    public @NotNull Vector2dc asVec2d() {
        return new Vector2dc(x, y);
    }

    @Override
    public @NotNull Vector2ic copy() {
        return new Vector2ic(x, y);
    }

    public @NotNull Vector2i asMutable() {
        return new Vector2i(x, y);
    }

    @Override
    public String toString() {
        return "Vector2ic{%d, %d}".formatted(x, y);
    }

    public static @NotNull Vector2ic of(@NotNull Vector2iInterface vector2iInterface) {
        return new Vector2ic(vector2iInterface.x(), vector2iInterface.y());
    }

    public static @NotNull Vector2i of(@NotNull Point2D point) {
        return new Vector2i((int)point.getX(), (int)point.getY());
    }

    public static @NotNull Vector2ic of(@NotNull org.joml.Vector2ic vector2ic) {
        return new Vector2ic(vector2ic.x(), vector2ic.y());
    }

    public static @NotNull Vector2ic createXY(int value) {
        return new Vector2ic(value, value);
    }
}
