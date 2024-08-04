package io.github.aratakileo.elegantia.math;

import org.jetbrains.annotations.NotNull;

public class Vector2f implements Vector2fInterface {
    public float x, y;

    public Vector2f(float @NotNull [] vector) {
        this.x = vector[0];
        this.y = vector[1];
    }

    public Vector2f(float x, float y) {
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
    public @NotNull Vector2f neg() {
        x = -x;
        y = -y;
        return this;
    }

    @Override
    public @NotNull Vector2f abs() {
        x = Math.abs(x);
        y = Math.abs(y);
        return this;
    }

    public @NotNull Vector2f set(int index, float value) {
        switch (index) {
            case 0 -> this.x = value;
            case 1 -> this.y = value;
            default -> throw new IllegalArgumentException();
        }

        return this;
    }

    @Override
    public @NotNull Vector2f sub(float value) {
        return sub(value, value);
    }

    @Override
    public @NotNull Vector2f sub(float x, float y) {
        this.x -= x;
        this.y -= y;
        return this;
    }

    @Override
    public @NotNull Vector2f sub(@NotNull Vector2iInterface vector2iInterface) {
        return sub(vector2iInterface.x(), vector2iInterface.y());
    }

    @Override
    public @NotNull Vector2f sub(@NotNull Vector2fInterface vector2FInterface) {
        return sub(vector2FInterface.x(), vector2FInterface.y());
    }

    @Override
    public @NotNull Vector2f sub(@NotNull org.joml.Vector2ic vector2ic) {
        return sub(vector2ic.x(), vector2ic.y());
    }

    @Override
    public @NotNull Vector2f sub(@NotNull org.joml.Vector2fc vector2ic) {
        return sub(vector2ic.x(), vector2ic.y());
    }

    @Override
    public @NotNull Vector2f add(float value) {
        return add(value, value);
    }

    @Override
    public @NotNull Vector2f add(float x, float y) {
        this.x += x;
        this.y += y;
        return this;
    }

    @Override
    public @NotNull Vector2f add(@NotNull Vector2iInterface vector2iInterface) {
        return add(vector2iInterface.x(), vector2iInterface.y());
    }

    @Override
    public @NotNull Vector2f add(@NotNull Vector2fInterface vector2FInterface) {
        return add(vector2FInterface.x(), vector2FInterface.y());
    }

    @Override
    public @NotNull Vector2f add(@NotNull org.joml.Vector2ic vector2ic) {
        return add(vector2ic.x(), vector2ic.y());
    }

    @Override
    public @NotNull Vector2f add(@NotNull org.joml.Vector2fc vector2ic) {
        return add(vector2ic.x(), vector2ic.y());
    }

    @Override
    public @NotNull Vector2f mul(float value) {
        return mul(value, value);
    }

    @Override
    public @NotNull Vector2f mul(float x, float y) {
        this.x *= x;
        this.y *= y;
        return this;
    }

    @Override
    public @NotNull Vector2f mul(@NotNull Vector2iInterface vector2iInterface) {
        return mul(vector2iInterface.x(), vector2iInterface.y());
    }

    @Override
    public @NotNull Vector2f mul(@NotNull Vector2fInterface vector2FInterface) {
        return mul(vector2FInterface.x(), vector2FInterface.y());
    }

    @Override
    public @NotNull Vector2f mul(@NotNull org.joml.Vector2ic vector2ic) {
        return mul(vector2ic.x(), vector2ic.y());
    }

    @Override
    public @NotNull Vector2f mul(@NotNull org.joml.Vector2fc vector2fc) {
        return mul(vector2fc.x(), vector2fc.y());
    }

    @Override
    public @NotNull Vector2f div(float value) {
        return div(value, value);
    }

    @Override
    public @NotNull Vector2f div(float x, float y) {
        this.x /= x;
        this.y /= y;

        return this;
    }

    @Override
    public @NotNull Vector2f div(@NotNull Vector2iInterface vector2iInterface) {
        return div(vector2iInterface.x(), vector2iInterface.y());
    }

    @Override
    public @NotNull Vector2f div(@NotNull Vector2fInterface vector2FInterface) {
        return div(vector2FInterface.x(), vector2FInterface.y());
    }

    @Override
    public @NotNull Vector2f div(@NotNull org.joml.Vector2ic vector2ic) {
        return div(vector2ic.x(), vector2ic.y());
    }

    @Override
    public @NotNull Vector2f div(@NotNull org.joml.Vector2fc vector2fc) {
        return div(vector2fc.x(), vector2fc.y());
    }

    @Override
    public @NotNull Vector2f floor() {
        x = (float) Math.floor(x);
        y = (float) Math.floor(y);
        return this;
    }

    @Override
    public @NotNull Vector2f ceil() {
        x = (float) Math.ceil(x);
        y = (float) Math.ceil(y);
        return this;
    }

    @Override
    public @NotNull Vector2f round() {
        x = Math.round(x);
        y = Math.round(y);
        return this;
    }

    @Override
    public @NotNull Vector2f copy() {
        return new Vector2f(x, y);
    }

    public @NotNull Vector2fc copyAsImmutable() {
        return new Vector2fc(x, y);
    }

    @Override
    public String toString() {
        return "Vector2d{%s, %s}".formatted(x, y);
    }

    public static @NotNull Vector2f of(@NotNull Vector2iInterface vector2iInterface) {
        return new Vector2f(vector2iInterface.x(), vector2iInterface.y());
    }

    public static @NotNull Vector2f of(@NotNull Vector2dInterface vector2dInterface) {
        return new Vector2f((float) vector2dInterface.x(), (float) vector2dInterface.y());
    }

    public static @NotNull Vector2f of(@NotNull Vector2fInterface vector2fInterface) {
        return new Vector2f(vector2fInterface.x(), vector2fInterface.y());
    }

    public static @NotNull Vector2f of(@NotNull org.joml.Vector2ic vector2ic) {
        return new Vector2f(vector2ic.x(), vector2ic.y());
    }

    public static @NotNull Vector2f of(@NotNull org.joml.Vector2dc vector2dc) {
        return new Vector2f((float) vector2dc.x(), (float) vector2dc.y());
    }

    public static @NotNull Vector2f of(@NotNull org.joml.Vector2fc vector2fc) {
        return new Vector2f(vector2fc.x(), vector2fc.y());
    }

    public static @NotNull Vector2f zero() {
        return new Vector2f(0, 0);
    }
}
