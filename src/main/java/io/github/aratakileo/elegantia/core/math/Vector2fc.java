package io.github.aratakileo.elegantia.core.math;

import org.jetbrains.annotations.NotNull;

public class Vector2fc implements Vector2fInterface {
    public static final Vector2fc ZERO = new Vector2fc(0, 0);

    public final float x, y;

    public Vector2fc(float @NotNull [] vector) {
        this.x = vector[0];
        this.y = vector[1];
    }

    public Vector2fc(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public float x() {
        return x;
    }

    @Override
    public float y() {
        return y;
    }

    @Override
    public @NotNull Vector2fc neg() {
        return new Vector2fc(-x, -y);
    }

    @Override
    public @NotNull Vector2fc abs() {
        return new Vector2fc(Math.abs(x), Math.abs(y));
    }

    @Override
    public @NotNull Vector2fc sub(float value) {
        return new Vector2fc(x - value, y - value);
    }

    @Override
    public @NotNull Vector2fc sub(float x, float y) {
        return new Vector2fc(this.x - x, this.y - y);
    }

    @Override
    public @NotNull Vector2fc sub(@NotNull Vector2iInterface vector2iInterface) {
        return new Vector2fc(x - (float) vector2iInterface.x(), y - (float) vector2iInterface.y());
    }

    @Override
    public @NotNull Vector2fc sub(@NotNull Vector2fInterface vector2FInterface) {
        return new Vector2fc(x - vector2FInterface.x(), y - vector2FInterface.y());
    }

    @Override
    public @NotNull Vector2fc sub(@NotNull org.joml.Vector2ic vector2ic) {
        return new Vector2fc(x - (float) vector2ic.x(), y - (float) vector2ic.y());
    }

    @Override
    public @NotNull Vector2fc sub(@NotNull org.joml.Vector2fc vector2fc) {
        return new Vector2fc(x - (float) vector2fc.x(), y - (float) vector2fc.y());
    }

    @Override
    public @NotNull Vector2fc add(float value) {
        return new Vector2fc(x + value, y + value);
    }

    @Override
    public @NotNull Vector2fc add(float x, float y) {
        return new Vector2fc(this.x + x, this.y + y);
    }

    @Override
    public @NotNull Vector2fc add(@NotNull Vector2iInterface vector2iInterface) {
        return new Vector2fc(x + (float) vector2iInterface.x(), y + (float) vector2iInterface.y());
    }

    @Override
    public @NotNull Vector2fc add(@NotNull Vector2fInterface vector2FInterface) {
        return new Vector2fc(x + vector2FInterface.x(), y + vector2FInterface.y());
    }

    @Override
    public @NotNull Vector2fc add(@NotNull org.joml.Vector2ic vector2ic) {
        return new Vector2fc(x + (float) vector2ic.x(), y + (float) vector2ic.y());
    }

    @Override
    public @NotNull Vector2fc add(@NotNull org.joml.Vector2fc vector2fc) {
        return new Vector2fc(x + (float) vector2fc.x(), y + (float) vector2fc.y());
    }

    @Override
    public @NotNull Vector2fc mul(float value) {
        return new Vector2fc(x * value, y * value);
    }

    @Override
    public @NotNull Vector2fc mul(float x, float y) {
        return new Vector2fc(this.x * x, this.y * y);
    }

    @Override
    public @NotNull Vector2fc mul(@NotNull Vector2iInterface vector2iInterface) {
        return new Vector2fc(x * vector2iInterface.x(), y * vector2iInterface.y());
    }

    @Override
    public @NotNull Vector2fc mul(@NotNull Vector2fInterface vector2FInterface) {
        return new Vector2fc(x * vector2FInterface.x(), y * vector2FInterface.y());
    }

    @Override
    public @NotNull Vector2fc mul(@NotNull org.joml.Vector2ic vector2ic) {
        return new Vector2fc(x * vector2ic.x(), y * vector2ic.y());
    }

    @Override
    public @NotNull Vector2fc mul(@NotNull org.joml.Vector2fc vector2fc) {
        return new Vector2fc(x * vector2fc.x(), y * vector2fc.y());
    }

    @Override
    public @NotNull Vector2fc div(float value) {
        return new Vector2fc(x / value, x / value);
    }

    @Override
    public @NotNull Vector2fc div(float x, float y) {
        return new Vector2fc(this.x / x, this.y / y);
    }

    @Override
    public @NotNull Vector2fc div(@NotNull Vector2iInterface vector2iInterface) {
        return new Vector2fc(x / vector2iInterface.x(), y / vector2iInterface.y());
    }

    @Override
    public @NotNull Vector2fc div(@NotNull Vector2fInterface vector2FInterface) {
        return new Vector2fc(x / vector2FInterface.x(), y / vector2FInterface.y());
    }

    @Override
    public @NotNull Vector2fc div(@NotNull org.joml.Vector2ic vector2ic) {
        return new Vector2fc(x / vector2ic.x(), y / vector2ic.y());
    }

    @Override
    public @NotNull Vector2fc div(@NotNull org.joml.Vector2fc vector2fc) {
        return new Vector2fc(x / vector2fc.x(), y / vector2fc.y());
    }

    @Override
    public @NotNull Vector2fc floor() {
        return new Vector2fc((float) Math.floor(x), (float) Math.floor(y));
    }

    @Override
    public @NotNull Vector2fc ceil() {
        return new Vector2fc((float) Math.floor(x), (float) Math.floor(y));
    }

    @Override
    public @NotNull Vector2fc round() {
        return new Vector2fc(Math.round(x), Math.round(y));
    }

    @Override
    public @NotNull Vector2fc copy() {
        return new Vector2fc(x, y);
    }

    @Override
    public @NotNull Vector2ic asVec2i() {
        return new Vector2ic((int)x, (int)y);
    }

    @Override
    public @NotNull Vector2dc asVec2d() {
        return new Vector2dc(x, y);
    }

    public @NotNull Vector2f asMutable() {
        return new Vector2f(x, y);
    }

    @Override
    public String toString() {
        return "Vector2fc{%s, %s}".formatted(x, y);
    }

    public static @NotNull Vector2fc of(@NotNull Vector2iInterface vector2iInterface) {
        return new Vector2fc(vector2iInterface.x(), vector2iInterface.y());
    }

    public static @NotNull Vector2fc of(@NotNull Vector2fInterface vector2fInterface) {
        return new Vector2fc(vector2fInterface.x(), vector2fInterface.y());
    }

    public static @NotNull Vector2fc of(@NotNull org.joml.Vector2ic vector2ic) {
        return new Vector2fc(vector2ic.x(), vector2ic.y());
    }

    public static @NotNull Vector2fc of(@NotNull org.joml.Vector2dc vector2dc) {
        return new Vector2fc((float) vector2dc.x(), (float) vector2dc.y());
    }

    public static @NotNull Vector2fc of(@NotNull org.joml.Vector2fc vector2fc) {
        return new Vector2fc(vector2fc.x(), vector2fc.y());
    }
}
