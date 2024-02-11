package io.github.aratakileo.elegantia.util;

import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2dc;
import org.joml.Vector2fc;
import org.joml.Vector2ic;

public class Rect2i extends net.minecraft.client.renderer.Rect2i {
    public Rect2i(int @NotNull [] rect) {
        super(rect[0], rect[1], rect[2], rect[3]);
    }

    public Rect2i(@NotNull Vector2ic vector2ic, int size) {
        this(vector2ic, size, size);
    }

    public Rect2i(@NotNull Vector2ic vector2ic, int width, int height) {
        super(vector2ic.x(), vector2ic.y(), width, height);
    }

    public Rect2i(int x, int y, int size) {
        super(x, y, size, size);
    }

    public Rect2i(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public boolean isExist() {
        return getWidth() > 0 && getHeight() > 0;
    }

    public int get(int index) {
        return switch (index) {
            case 0 -> getX();
            case 1 -> getY();
            case 2 -> getWidth();
            case 3 -> getHeight();
            default -> throw new IllegalArgumentException();
        };
    }

    public @NotNull Rect2i set(int index, int value) {
        return switch (index) {
            case 0 -> setIpX(value);
            case 1 -> setIpY(value);
            case 2 -> setIpWidth(value);
            case 3 -> setIpHeight(value);
            default -> throw new IllegalArgumentException();
        };
    }

    @Override
    public void setX(int x) {
        super.setX(x);
    }

    public @NotNull Rect2i setIpX(int x) {
        setX(x);
        return this;
    }

    @Override
    public void setY(int y) {
        super.setY(y);
    }

    public @NotNull Rect2i setIpY(int y) {
        setY(y);
        return this;
    }

    public void setPosition(@NotNull Vector2ic position) {
        super.setPosition(position.x(), position.y());
    }

    @Override
    public void setPosition(int x, int y) {
        super.setPosition(x, y);
    }

    public @NotNull Rect2i setIpPosition(@NotNull Vector2ic position) {
        setPosition(position);
        return this;
    }

    public @NotNull Rect2i setIpPosition(int x, int y) {
        setPosition(x, y);
        return this;
    }

    public int getLeft() {
        return getX();
    }

    public void setLeft(int left) {
        setX(left);
    }

    public @NotNull Rect2i setIpLeft(int left) {
        setX(left);
        return this;
    }

    public int getRight() {
        return getX() + getWidth();
    }

    public void setRight(int right) {
        setX(right - getWidth());
    }

    public @NotNull Rect2i setIpRight(int right) {
        setRight(right);
        return this;
    }

    public int getTop() {
        return getY();
    }

    public void setTop(int top) {
        setY(top);
    }

    public @NotNull Rect2i setIpTop(int top) {
        setY(top);
        return this;
    }

    public int getBottom() {
        return getY() + getHeight();
    }

    public void setBottom(int bottom) {
        setY(bottom - getHeight());
    }

    public @NotNull Rect2i setIpBottom(int bottom) {
        setBottom(bottom);
        return this;
    }

    public void setLeftTop(@NotNull Vector2ic vector2ic) {
        setPosition(vector2ic);
    }

    public void setLeftTop(int left, int top) {
        setPosition(left, top);
    }

    public @NotNull Rect2i setIpLeftTop(@NotNull Vector2ic vector2ic) {
        setPosition(vector2ic);
        return this;
    }

    public @NotNull Rect2i setIpLeftTop(int left, int top) {
        setPosition(left, top);
        return this;
    }

    public void setRightBottom(@NotNull Vector2ic vector2ic) {
        setRight(vector2ic.x());
        setBottom(vector2ic.y());
    }

    public void setRightBottom(int right, int bottom) {
        setRight(right);
        setBottom(bottom);
    }

    public @NotNull Rect2i setIpRightBottom(@NotNull Vector2ic vector2ic) {
        setRightBottom(vector2ic);
        return this;
    }

    public @NotNull Rect2i setIpRightBottom(int right, int bottom) {
        setRightBottom(right, bottom);
        return this;
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
    }

    public @NotNull Rect2i setIpWidth(int width) {
        setWidth(width);
        return this;
    }

    @Override
    public void setHeight(int height) {
        super.setHeight(height);
    }

    public @NotNull Rect2i setIpHeight(int height) {
        setHeight(height);
        return this;
    }

    public void setSize(int width, int height) {
        setWidth(width);
        setHeight(height);
    }

    public @NotNull Rect2i setIpSize(int width, int height) {
        setSize(width, height);

        return this;
    }

    public boolean contains(@NotNull Vector2ic vector2ic) {
        return super.contains(vector2ic.x(), vector2ic.y());
    }

    @Override
    public boolean contains(int x, int y) {
        return super.contains(x, y);
    }

    public boolean contains(@NotNull Vector2fc vector2fc) {
        return contains(vector2fc.x(), vector2fc.y());
    }

    public boolean contains(float x, float y) {
        return contains((int) Math.ceil(x), (int) Math.ceil(y));
    }

    public boolean contains(@NotNull Vector2dc vector2dc) {
        return contains(vector2dc.x(), vector2dc.y());
    }

    public boolean contains(double x, double y) {
        return contains((int) Math.ceil(x), (int) Math.ceil(y));
    }

    public @NotNull Rect2i moveX(int x) {
        return copy().moveIpX(x);
    }

    public @NotNull Rect2i moveIpX(int x) {
        setX(getX() + x);
        return this;
    }

    public @NotNull Rect2i moveY(int y) {
        return copy().moveIpY(y);
    }

    public @NotNull Rect2i moveIpY(int y) {
        setY(getY() + y);
        return this;
    }

    public @NotNull Rect2i move(@NotNull Vector2ic vector2ic) {
        return copy().moveIp(vector2ic);
    }

    public @NotNull Rect2i move(int x, int y) {
        return copy().moveIp(x, y);
    }

    public @NotNull Rect2i moveIp(@NotNull Vector2ic vector2ic) {
        setX(getX() + vector2ic.x());
        setY(getY() + vector2ic.y());

        return this;
    }

    public @NotNull Rect2i moveIp(int x, int y) {
        setX(getX() + x);
        setY(getY() + y);

        return this;
    }

    public @NotNull Rect2i cutLeft(int length) {
        return copy().cutIpLeft(length);
    }

    public @NotNull Rect2i moveHorizontal(int left, int right) {
        return copy().moveIpHorizontal(left, right);
    }

    public @NotNull Rect2i cutIpLeft(int length) {
        return moveIpHorizontal(length, 0);
    }

    public @NotNull Rect2i moveIpHorizontal(int left, int right) {
        moveIpX(left);
        setWidth(getWidth() - left + right);

        if (getWidth() < 0) {
            moveIpX(getWidth());
            setWidth(-getWidth());
        }

        return this;
    }

    public @NotNull Rect2i cutTop(int length) {
        return copy().cutIpTop(length);
    }

    public @NotNull Rect2i moveVertical(int top, int bottom) {
        return copy().moveIpVertical(top, bottom);
    }

    public @NotNull Rect2i cutIpTop(int length) {
        return moveIpVertical(length, 0);
    }

    public @NotNull Rect2i moveIpVertical(int top, int bottom) {
        moveIpY(top);
        setHeight(getHeight() - top + bottom);

        if (getHeight() < 0) {
            moveIpY(getHeight());
            setHeight(-getHeight());
        }

        return this;
    }

    public @NotNull Rect2i moveBounds(int left, int top, int right, int bottom) {
        return copy().moveIpBounds(left, top, right, bottom);
    }

    public @NotNull Rect2i moveIpBounds(int left, int top, int right, int bottom) {
        moveIpHorizontal(left, right);
        moveIpVertical(top, bottom);

        return this;
    }

    public @NotNull Rect2i expandHorizontal(int horizontal) {
        return copy().setIpWidth(getWidth() + horizontal);
    }

    public @NotNull Rect2i expandVertical(int vertical) {
        return copy().setIpHeight(getHeight() + vertical);
    }

    public @NotNull Rect2i expand(int horizontal, int vertical) {
        return copy().setIpSize(getWidth() + horizontal, getHeight() + vertical);
    }

    @Override
    public @NotNull String toString() {
        return "Rect2i{%d, %d, %d, %d}".formatted(getX(), getY(), getWidth(), getHeight());
    }

    public @NotNull Rect2i copy() {
        return new Rect2i(getX(), getY(), getWidth(), getHeight());
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
}
