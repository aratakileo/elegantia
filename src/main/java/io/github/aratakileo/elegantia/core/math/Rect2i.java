package io.github.aratakileo.elegantia.core.math;

import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.geom.Point2D;

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

    public int getCenterX() {
        return x + width / 2;
    }

    public @NotNull Rect2i setCenterX(int centerX) {
        x = centerX - width / 2;
        return this;
    }

    public int getCenterY() {
        return y + height / 2;
    }

    public @NotNull Rect2i setCenterY(int centerY) {
        y = centerY - height / 2;
        return this;
    }

    public @NotNull Vector2ic getCenterPos() {
        return new Vector2ic(x + width / 2, y + height / 2);
    }

    public @NotNull Rect2i setCenter(@NotNull Vector2iInterface center) {
        x = center.x() - width / 2;
        y = center.y() - height / 2;
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

    public @NotNull Vector2ic getLeftBottom() {
        return new Vector2ic(x, y + height);
    }

    public @NotNull Rect2i setLeftBottom(@NotNull Vector2iInterface leftBottom) {
        x = leftBottom.x();
        y = leftBottom.y() - height;

        return this;
    }

    public @NotNull Rect2i setLeftBottom(int left, int bottom) {
        x = left;
        y = bottom - height;

        return this;
    }

    public @NotNull Vector2ic getRightBottom() {
        return new Vector2ic(x + width, y + height);
    }

    public @NotNull Rect2i setRightBottom(@NotNull Vector2iInterface rightBottom) {
        x = rightBottom.x() - width;
        y = rightBottom.y() - height;
        
        return this;
    }

    public @NotNull Rect2i setRightBottom(int right, int bottom) {
        x = right - width;
        y = bottom - height;
        
        return this;
    }

    public @NotNull Vector2ic getRightTop() {
        return new Vector2ic(x + width, y);
    }

    public @NotNull Rect2i setRightTop(@NotNull Vector2iInterface rightTop) {
        x = rightTop.x() - width;
        y = rightTop.y();

        return this;
    }

    public @NotNull Rect2i setRightTop(int right, int top) {
        x = right - width;
        y = top;

        return this;
    }

    public @NotNull Vector2ic getCornerPos(@NotNull Corner corner) {
        return switch (corner) {
            case LEFT_TOP -> getLeftTop();
            case LEFT_BOTTOM -> getLeftBottom();
            case RIGHT_BOTTOM -> getRightBottom();
            case RIGHT_TOP -> getRightTop();
        };
    }

    public @NotNull Rect2i setCornerPos(@NotNull Corner corner, @NotNull Vector2iInterface pos) {
        switch (corner) {
            case LEFT_TOP -> setLeftTop(pos);
            case LEFT_BOTTOM -> setLeftBottom(pos);
            case RIGHT_BOTTOM -> setRightBottom(pos);
            case RIGHT_TOP -> setRightTop(pos);
        };

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

    public @NotNull Size2ic getSize() {
        return new Size2ic(width, height);
    }

    @SuppressWarnings("UnusedReturnValue")
    public @NotNull Rect2i setSize(int size) {
        width = size;
        height = size;

        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public @NotNull Rect2i setSize(@NotNull Size2iInterface size) {
        width = size.width();
        height = size.height();

        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public @NotNull Rect2i setSize(int width, int height) {
        this.width = width;
        this.height = height;

        return this;
    }

    public boolean contains(@NotNull Point2D point) {
        return contains(point.getX(), point.getY());
    }

    public boolean contains(@NotNull Vector2iInterface position) {
        return contains(position.x(), position.y());
    }

    public boolean contains(@NotNull Vector2fInterface position) {
        return contains(position.x(), position.y());
    }

    public boolean contains(@NotNull Vector2dInterface position) {
        return contains(position.x(), position.y());
    }

    public boolean contains(double x, double y) {
        return contains((int) Math.ceil(x), (int) Math.ceil(y));
    }

    public boolean contains(float x, float y) {
        return contains((int) Math.ceil(x), (int) Math.ceil(y));
    }

    public boolean contains(int x, int y) {
        return x >= this.x && x <= this.x + this.width && y >= this.y && y <= this.y + this.height;
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

    public @NotNull Rect2i move(int units) {
        x += units;
        y += units;

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

    public @NotNull Rect2i moveBackX(int x) {
        this.x -= x;
        return this;
    }

    public @NotNull Rect2i moveBackY(int y) {
        this.y -= y;
        return this;
    }

    public @NotNull Rect2i moveBack(int units) {
        x -= units;
        y -= units;

        return this;
    }

    public @NotNull Rect2i moveBack(@NotNull Vector2iInterface position) {
        x -= position.x();
        y -= position.y();

        return this;
    }

    public @NotNull Rect2i moveBack(int x, int y) {
        this.x -= x;
        this.y -= y;

        return this;
    }

    public @NotNull Rect2i cut(@NotNull Vector2iInterface vec2i) {
        return cut(vec2i.x(), vec2i.y());
    }

    public @NotNull Rect2i cut(int left, int top) {
        return moveHorizontal(left, 0).moveVertical(top, 0);
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

    public @NotNull Rect2i expandBounds(int units) {
        return moveBounds(-units, -units, units, units);
    }


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

    public @NotNull Rect2i expand(int units) {
        width += units;
        height += units;

        return this;
    }

    public @NotNull Rect2i expand(@NotNull Vector2iInterface vec2i) {
        width += vec2i.x();
        height += vec2i.y();

        return this;
    }

    public @NotNull Rect2i expand(@NotNull Size2iInterface size) {
        width += size.width();
        height += size.height();

        return this;
    }

    public @NotNull Rect2i expand(int horizontal, int vertical) {
        width += horizontal;
        height += vertical;

        return this;
    }

    public @NotNull Rect2i shrinkHorizontal(int horizontal) {
        width -= horizontal;
        return this;
    }

    public @NotNull Rect2i shrinkVertical(int vertical) {
        height -= vertical;
        return this;
    }

    public @NotNull Rect2i shrink(int units) {
        width -= units;
        height -= units;

        return this;
    }

    public @NotNull Rect2i shrink(@NotNull Vector2iInterface vec2i) {
        width -= vec2i.x();
        height -= vec2i.y();

        return this;
    }

    public @NotNull Rect2i shrink(@NotNull Size2iInterface size) {
        width -= size.width();
        height -= size.height();

        return this;
    }

    public @NotNull Rect2i shrink(int horizontal, int vertical) {
        width -= horizontal;
        height -= vertical;

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

    public @NotNull Rect2i copy() {
        return new Rect2i(x, y, width, height);
    }

    public @NotNull Rectangle asRectangle() {
        return new Rectangle(x, y, width, height);
    }
    
    public @NotNull net.minecraft.client.renderer.Rect2i asMinecraftRect2i() {
        return new net.minecraft.client.renderer.Rect2i(x, y, width, height);
    }
    
    public int @NotNull[] toArray() {
        return new int[]{ x, y, width, height };
    }

    @Override
    public @NotNull String toString() {
        return "Rect2i{%d, %d, %d, %d}".formatted(x, y, width, height);
    }

    public static @NotNull Rect2i of(int @NotNull [] rect) {
        return new Rect2i(rect[0], rect[1], rect[2], rect[3]);
    }

    public static @NotNull Rect2i of(@NotNull Vector2iInterface position, @NotNull Size2iInterface size) {
        return new Rect2i(position.x(), position.y(), size.width(), size.height());
    }

    public static @NotNull Rect2i of(int x, int y, @NotNull Size2iInterface size) {
        return new Rect2i(x, y, size.width(), size.height());
    }

    public static @NotNull Rect2i of(@NotNull Vector2iInterface position, int width, int height) {
        return new Rect2i(position.x(), position.y(), width, height);
    }

    public static @NotNull Rect2i of(@NotNull Rectangle rect) {
        return new Rect2i(
                rect.x,
                rect.y,
                rect.width,
                rect.height
        );
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

    public static @NotNull Rect2i square(@NotNull Vector2iInterface position, int size) {
        return new Rect2i(position.x(), position.y(), size, size);
    }

    public static @NotNull Rect2i square(int x, int y, int size) {
        return new Rect2i(x, y, size, size);
    }

    public static @NotNull Rect2i empty() {
        return new Rect2i(0, 0, 0, 0);
    }

    public static @NotNull Rect2i zeroPositionSquare(int size) {
        return new Rect2i(0, 0, size, size);
    }

    public static @NotNull Rect2i zeroPosition(int width, int height) {
        return new Rect2i(0, 0, width, height);
    }
}
