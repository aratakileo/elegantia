package io.github.aratakileo.elegantia.util;

import net.minecraft.client.gui.layouts.LayoutElement;
import org.jetbrains.annotations.NotNull;

public class Rect2i extends net.minecraft.client.renderer.Rect2i {
    public Rect2i(int x, int y, int size) {
        this(x, y, size, size);
    }

    public Rect2i(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public void setX(int x) {
        super.setX(x);
    }

    @Override
    public void setY(int y) {
        super.setY(y);
    }

    @Override
    public void setPosition(int x, int y) {
        super.setPosition(x, y);
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
    }

    @Override
    public void setHeight(int height) {
        super.setHeight(height);
    }

    @Override
    public boolean contains(int x, int y) {
        return super.contains(x, y);
    }

    public @NotNull Rect2i moveX(int x) {
        return copy().moveIpX(x);
    }

    public @NotNull Rect2i moveY(int y) {
        return copy().moveIpY(y);
    }

    public @NotNull Rect2i move(int x, int y) {
        return copy().moveIp(x, y);
    }

    public @NotNull Rect2i moveHorizontal(int left, int right) {
        return copy().moveIpHorizontal(left, right);
    }

    public @NotNull Rect2i moveVertical(int top, int bottom) {
        return copy().moveIpVertical(top, bottom);
    }

    public @NotNull Rect2i moveBounds(int left, int top, int right, int bottom) {
        return copy().moveIpBounds(left, top, right, bottom);
    }

    public @NotNull Rect2i moveIpX(int x) {
        setX(getX() + x);
        return this;
    }

    public @NotNull Rect2i moveIpY(int y) {
        setY(getY() + y);
        return this;
    }

    public @NotNull Rect2i moveIp(int x, int y) {
        setX(getX() + x);
        setY(getY() + y);

        return this;
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

    public @NotNull Rect2i moveIpVertical(int top, int bottom) {
        moveIpY(top);
        setHeight(getHeight() - top + bottom);

        if (getHeight() < 0) {
            moveIpY(getHeight());
            setHeight(-getHeight());
        }

        return this;
    }

    public @NotNull Rect2i moveIpBounds(int left, int top, int right, int bottom) {
        moveIpHorizontal(left, right);
        moveIpVertical(top, bottom);

        return this;
    }

    public boolean contains(float x, float y) {
        return contains((int) Math.ceil(x), (int) Math.ceil(y));
    }

    public boolean contains(double x, double y) {
        return contains((int) Math.ceil(x), (int) Math.ceil(y));
    }

    public @NotNull Rect2i expand(int horizontal, int vertical) {
        final var returnable = copy();
        returnable.setSize(getWidth() + horizontal, getHeight() + vertical);

        return returnable;
    }

    public void setSize(int width, int height) {
        setWidth(width);
        setHeight(height);
    }

    public int getLeft() {
        return getX();
    }

    public void setLeft(int left) {
        setX(left);
    }

    public int getRight() {
        return getX() + getWidth();
    }

    public void setRight(int right) {
        setX(right - getWidth());
    }

    public int getTop() {
        return getY();
    }

    public void setTop(int top) {
        setY(top);
    }

    public int getBottom() {
        return getY() + getHeight();
    }

    public void setBottom(int bottom) {
        setY(bottom - getHeight());
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
}
