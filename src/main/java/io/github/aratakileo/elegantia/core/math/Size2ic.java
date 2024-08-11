package io.github.aratakileo.elegantia.core.math;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class Size2ic implements Size2iInterface {
    public final int width, height;

    public Size2ic(int @NotNull[] size) {
        this.width = size[0];
        this.height = size[1];
    }

    public Size2ic(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public int width() {
        return width;
    }

    @Override
    public int height() {
        return height;
    }

    @Override
    public @NotNull Size2ic expand(int size) {
        return new Size2ic(width + size, height + size);
    }

    @Override
    public @NotNull Size2ic expand(@NotNull Size2iInterface size) {
        return new Size2ic(width + size.width(), height + size.height());
    }

    @Override
    public @NotNull Size2ic expand(@NotNull Vector2iInterface vec2i) {
        return new Size2ic(width + vec2i.x(), height + vec2i.y());
    }

    @Override
    public @NotNull Size2ic expand(int width, int height) {
        return new Size2ic(this.width + width, this.height + height);
    }

    @Override
    public @NotNull Size2ic shrink(int size) {
        return new Size2ic(width - size, height - size);
    }

    @Override
    public @NotNull Size2ic shrink(@NotNull Size2iInterface size) {
        return new Size2ic(width - size.width(), height - size.height());
    }

    @Override
    public @NotNull Size2ic shrink(@NotNull Vector2iInterface vec2i) {
        return new Size2ic(width - vec2i.x(), height - vec2i.y());
    }

    @Override
    public @NotNull Size2ic shrink(int width, int height) {
        return new Size2ic(this.width - width, this.height - height);
    }

    @Override
    public @NotNull Size2ic scale(int value) {
        return new Size2ic(width * value, height * value);
    }

    @Override
    public @NotNull Size2ic scale(float value) {
        return new Size2ic((int) (width * value), (int) (height * value));
    }

    @Override
    public @NotNull Size2ic scale(double value) {
        return new Size2ic((int) (width * value), (int) (height * value));
    }

    @Override
    public @NotNull Size2ic max(@NotNull Size2iInterface size2iInterface) {
        return new Size2ic(Math.max(width, size2iInterface.width()), Math.max(height, size2iInterface.height()));
    }

    @Override
    public @NotNull Size2ic min(@NotNull Size2iInterface size2iInterface) {
        return new Size2ic(Math.min(width, size2iInterface.width()), Math.min(height, size2iInterface.height()));
    }

    public @NotNull Size2ic copy() {
        return new Size2ic(width, height);
    }

    @Override
    public @NotNull Vector2ic asVec2i() {
        return new Vector2ic(width, height);
    }

    @Override
    public @NotNull Vector2dc asVec2d() {
        return new Vector2dc(width, height);
    }

    @Override
    public @NotNull Vector2fc asVec2f() {
        return new Vector2fc(width, height);
    }

    public @NotNull Size2i copyAsMutable() {
        return new Size2i(width, height);
    }

    public static @NotNull Size2ic of(@NotNull Dimension dimension) {
        return new Size2ic(dimension.width, dimension.height);
    }

    public static @NotNull Size2ic of(@NotNull Size2iInterface size2iInterface) {
        return new Size2ic(size2iInterface.width(), size2iInterface.height());
    }

    public static @NotNull Size2ic ofSquare(int size) {
        return new Size2ic(size, size);
    }

    @Override
    public String toString() {
        return "Size2ic{%s, %s}".formatted(width, height);
    }
}
