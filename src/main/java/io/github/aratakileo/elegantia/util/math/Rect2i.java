package io.github.aratakileo.elegantia.util.math;

import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2fc;

public class Rect2i {
    public int x, y, width, height;
    
    public Rect2i(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public boolean isEmpty() {
        return width > 0 && height > 0;
    }

    public int get(int index) {
        return switch (index) {
            case 0 -> x;
            case 1 -> y;
            case 2 -> width;
            case 3 -> height;
            default -> throw new IllegalArgumentException();
        };
    }

    public @NotNull Rect2i set(int index, int value) {
        return switch (index) {
            case 0 -> setX(value);
            case 1 -> setY(value);
            case 2 -> setWidth(value);
            case 3 -> setHeight(value);
            default -> throw new IllegalArgumentException();
        };
    }

    public @NotNull Rect2i setX(int x) {
        this.x = x;
        return this;
    }

    public @NotNull Rect2i setY(int y) {
        this.y = y;
        return this;
    }

    public @NotNull Vector2ic getPosition() {
        return new Vector2ic(x, y);
    }

    @SuppressWarnings("UnusedReturnValue")
    public @NotNull Rect2i setPosition(@NotNull Vector2iInterface position) {
        x = position.x();
        y = position.y();
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public @NotNull Rect2i setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public int getLeft() {
        return x;
    }

    @SuppressWarnings("UnusedReturnValue")
    public @NotNull Rect2i setLeft(int left) {
        x = left;
        return this;
    }

    public int getRight() {
        return x + width;
    }

    @SuppressWarnings("UnusedReturnValue")
    public @NotNull Rect2i setRight(int right) {
        x = right - width;
        return this;
    }

    public int getTop() {
        return y;
    }

    @SuppressWarnings("UnusedReturnValue")
    public @NotNull Rect2i setTop(int top) {
        y = top;
        return this;
    }

    public int getBottom() {
        return y + height;
    }

    @SuppressWarnings("UnusedReturnValue")
    public @NotNull Rect2i setBottom(int bottom) {
        y = bottom - height;
        return this;
    }

    public @NotNull Vector2ic getLeftTop() {
        return new Vector2ic(x, y);
    }

    public @NotNull Rect2i setLeftTop(@NotNull Vector2iInterface leftTop) {
        x = leftTop.x();
        y = leftTop.y();

        return this;
    }

    public @NotNull Rect2i setLeftTop(int left, int top) {
        x = left;
        y = top;
        
        return this;
    }

    public @NotNull Vector2ic getRightBottom() {
        return new Vector2ic(x + width, y + height);
    }

    public @NotNull Rect2i setIpRightBottom(@NotNull Vector2iInterface rightBottom) {
        x = rightBottom.x() - width;
        y = rightBottom.y() - height;
        
        return this;
    }

    public @NotNull Rect2i setIpRightBottom(int right, int bottom) {
        x = right - width;
        y = bottom - height;
        
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public @NotNull Rect2i setWidth(int width) {
        this.width = width;
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public @NotNull Rect2i setHeight(int height) {
        this.height = height;
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public @NotNull Rect2i setSize(int width, int height) {
        this.width = width;
        this.height = height;

        return this;
    }

    public boolean contains(@NotNull Vector2iInterface position) {
        return contains(position.x(), position.y());
    }
    
    public boolean contains(int x, int y) {
        return x >= this.x && x <= this.x + this.width && y >= this.y && y <= this.y + this.height;
    }

    public boolean contains(@NotNull Vector2fc position) {
        return contains(position.x(), position.y());
    }

    public boolean contains(float x, float y) {
        return contains((int) Math.ceil(x), (int) Math.ceil(y));
    }

    public boolean contains(@NotNull Vector2dInterface position) {
        return contains(position.x(), position.y());
    }

    public boolean contains(double x, double y) {
        return contains((int) Math.ceil(x), (int) Math.ceil(y));
    }

    @SuppressWarnings("UnusedReturnValue")
    public @NotNull Rect2i moveX(int x) {
        this.x += x;
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public @NotNull Rect2i moveY(int y) {
        this.y += y;
        return this;
    }

    public @NotNull Rect2i move(@NotNull Vector2iInterface position) {
        x += position.x();
        y += position.y();

        return this;
    }

    public @NotNull Rect2i move(int x, int y) {
        this.x += x;
        this.y += y;

        return this;
    }

    public @NotNull Rect2i cutLeft(int length) {
        return moveHorizontal(length, 0);
    }

    public @NotNull Rect2i moveHorizontal(int left, int right) {
        x += left;
        width = width - left + right;

        if (width < 0) {
            x += width;
            width = -width;
        }

        return this;
    }

    public @NotNull Rect2i cutTop(int length) {
        return moveVertical(length, 0);
    }

    public @NotNull Rect2i moveVertical(int top, int bottom) {
        y += top;
        height = height - top + bottom;

        if (height < 0) {
            y += height;
            height = -height;
        }

        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public @NotNull Rect2i moveBounds(int left, int top, int right, int bottom) {
        return moveHorizontal(left, right).moveVertical(top, bottom);
    }

    public @NotNull Rect2i expandHorizontal(int horizontal) {
        width += horizontal;
        return this;
    }

    public @NotNull Rect2i expandVertical(int vertical) {
        height += vertical;
        return this;
    }

    public @NotNull Rect2i expand(int horizontal, int vertical) {
        width += horizontal;
        height += vertical;

        return this;
    }

    public @NotNull Rect2i mul(int value) {
        return new Rect2i(x * value, y * value, width * value, height * value);
    }

    public @NotNull Rect2i mul(double value) {
        return new Rect2i(
                (int) (x * value),
                (int) (y * value),
                (int) (width * value),
                (int) (height * value)
        );
    }

    public @NotNull Rect2i mul(float value) {
        return new Rect2i(
                (int) (x * value),
                (int) (y * value),
                (int) (width * value),
                (int) (height * value)
        );
    }

    @Override
    public @NotNull String toString() {
        return "Rect2i{%d, %d, %d, %d}".formatted(x, y, width, height);
    }

    public @NotNull Rect2i copy() {
        return new Rect2i(x, y, width, height);
    }
    
    public @NotNull net.minecraft.client.renderer.Rect2i toNativeRect2i() {
        return new net.minecraft.client.renderer.Rect2i(x, y, width, height);
    }
    
    public int @NotNull[] toArray() {
        return new int[]{ x, y, width, height };
    }

    public static @NotNull Rect2i of(int @NotNull [] rect) {
        return new Rect2i(rect[0], rect[1], rect[2], rect[3]);
    }

    public static @NotNull Rect2i of(@NotNull Vector2iInterface position, int size) {
        return new Rect2i(position.x(), position.y(), size, size);
    }

    public static @NotNull Rect2i of(@NotNull Vector2iInterface position, int width, int height) {
        return new Rect2i(position.x(), position.y(), width, height);
    }

    public static @NotNull Rect2i of(int x, int y, int size) {
        return new Rect2i(x, y, size, size);
    }

    public static @NotNull Rect2i of(@NotNull LayoutElement layoutElement) {
        return new Rect2i(
                layoutElement.getX(),
                layoutElement.getY(),
                layoutElement.getWidth(),
                layoutElement.getHeight()
        );
    }

    public static @NotNull Rect2i of(@NotNull net.minecraft.client.renderer.Rect2i rect2i) {
        return new Rect2i(rect2i.getX(), rect2i.getY(), rect2i.getWidth(), rect2i.getHeight());
    }
    
    public static @NotNull Rect2i of(@NotNull ScreenRectangle screenRectangle) {
        return new Rect2i(
                screenRectangle.left(),
                screenRectangle.top(),
                screenRectangle.width(),
                screenRectangle.height()
        );
    }

    public static @NotNull Rect2i empty() {
        return new Rect2i(0, 0, 0, 0);
    }

    public static @NotNull Rect2i startPosition(int width, int height) {
        return new Rect2i(0, 0, width, height);
    }
}
