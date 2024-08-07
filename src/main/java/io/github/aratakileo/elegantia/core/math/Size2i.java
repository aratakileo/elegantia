package io.github.aratakileo.elegantia.core.math;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class Size2i implements Size2iInterface{
    public int width, height;

    public Size2i(int @NotNull [] size) {
        this.width = size[0];
        this.height = size[1];
    }

    public Size2i(int width, int height) {
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

    public @NotNull Size2i set(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    @Override
    public @NotNull Size2i add(int size) {
        width += size;
        height += size;
        return this;
    }

    @Override
    public @NotNull Size2i add(@NotNull Size2iInterface size) {
        width += size.width();
        height += size.height();
        return this;
    }

    @Override
    public @NotNull Size2i add(int width, int height) {
        this.width += width;
        this.height += height;
        return this;
    }

    @Override
    public @NotNull Size2i sub(int size) {
        width -= size;
        height -= size;
        return this;
    }

    @Override
    public @NotNull Size2i sub(@NotNull Size2iInterface size) {
        width -= size.width();
        height -= size.height();
        return this;
    }

    @Override
    public @NotNull Size2i sub(int width, int height) {
        this.width -= width;
        this.height -= height;
        return this;
    }

    @Override
    public @NotNull Size2i scale(int value) {
        width *= value;
        height *= value;
        return this;
    }

    @Override
    public @NotNull Size2i scale(float value) {
        width *= value;
        height *= value;
        return this;
    }

    @Override
    public @NotNull Size2i scale(double value) {
        width *= value;
        height *= value;
        return this;
    }

    public @NotNull Size2i copy() {
        return new Size2i(width, height);
    }

    public @NotNull Size2ic copyAsImmutable() {
        return new Size2ic(width, height);
    }

    public static @NotNull Size2i of(@NotNull Dimension dimension) {
        return new Size2i(dimension.width, dimension.height);
    }

    public static @NotNull Size2i of(@NotNull Size2iInterface size2iInterface) {
        return new Size2i(size2iInterface.width(), size2iInterface.height());
    }

    public static @NotNull Size2i ofSquare(int size) {
        return new Size2i(size, size);
    }

    @Override
    public String toString() {
        return "Size2i{%s, %s}".formatted(width, height);
    }
}
