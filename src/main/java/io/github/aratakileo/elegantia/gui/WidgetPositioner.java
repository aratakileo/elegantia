package io.github.aratakileo.elegantia.gui;

import io.github.aratakileo.elegantia.math.Rect2i;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class WidgetPositioner {
    public static final int GRAVITY_LEFT = 0,
            GRAVITY_TOP = 0,
            GRAVITY_RIGHT = 1,
            GRAVITY_BOTTOM = 1 << 1,
            GRAVITY_CENTER_HORIZONTAL = 1 << 2,
            GRAVITY_CENTER_VERTICAL = 1 << 3,
            GRAVITY_CENTER = GRAVITY_CENTER_HORIZONTAL | GRAVITY_CENTER_VERTICAL;

    private final int contentWidth, contentHeight;
    private int paddingLeft = 0, paddingTop = 0, paddingRight = 0, paddingBottom = 0;
    private int marginLeft = 0, marginTop = 0, marginRight = 0, marginBottom = 0;
    private int gravity = GRAVITY_LEFT | GRAVITY_TOP;

    public WidgetPositioner(int contentSize) {
        this.contentWidth = contentSize;
        this.contentHeight = contentSize;
    }

    public WidgetPositioner(int contentWidth, int contentHeight) {
        this.contentWidth = contentWidth;
        this.contentHeight = contentHeight;
    }

    public @NotNull WidgetPositioner setPadding(int padding) {
        paddingLeft = padding;
        paddingTop = padding;
        paddingRight = padding;
        paddingBottom = padding;

        return this;
    }

    public @NotNull WidgetPositioner setPadding(int paddingLeft, int paddingTop, int paddingRight, int paddingBottom) {
        this.paddingLeft = paddingLeft;
        this.paddingTop = paddingTop;
        this.paddingRight = paddingRight;
        this.paddingBottom = paddingBottom;

        return this;
    }

    public @NotNull WidgetPositioner setPaddingLeft(int paddingLeft) {
        this.paddingLeft = paddingLeft;
        return this;
    }

    public @NotNull WidgetPositioner setPaddingTop(int paddingTop) {
        this.paddingTop = paddingTop;
        return this;
    }

    public @NotNull WidgetPositioner setPaddingRight(int paddingRight) {
        this.paddingRight = paddingRight;
        return this;
    }

    public @NotNull WidgetPositioner setPaddingBottom(int paddingBottom) {
        this.paddingBottom = paddingBottom;
        return this;
    }

    public @NotNull WidgetPositioner setMargin(int margin) {
        marginLeft = margin;
        marginTop = margin;
        marginRight = margin;
        marginBottom = margin;
        return this;
    }

    public @NotNull WidgetPositioner setMargin(int marginLeft, int marginTop, int marginRight, int marginBottom) {
        this.marginLeft = marginLeft;
        this.marginTop = marginTop;
        this.marginRight = marginRight;
        this.marginBottom = marginBottom;
        return this;
    }

    public @NotNull WidgetPositioner setMarginLeft(int marginLeft) {
        this.marginLeft = marginLeft;
        return this;
    }

    public @NotNull WidgetPositioner setMarginTop(int marginTop) {
        this.marginTop = marginTop;
        return this;
    }

    public @NotNull WidgetPositioner setMarginRight(int marginRight) {
        this.marginRight = marginRight;
        return this;
    }

    public @NotNull WidgetPositioner setMarginBottom(int marginBottom) {
        this.marginBottom = marginBottom;
        return this;
    }

    public @NotNull WidgetPositioner setGravity(int gravity) {
        this.gravity = gravity;
        return this;
    }

    public @NotNull Rect2i getNewBounds() {
        return getNewBounds(null, null);
    }

    public @NotNull Rect2i getNewBoundsOf(@Nullable Rect2i oldBounds) {
        return getNewBounds(null, oldBounds);
    }

    public @NotNull Rect2i getNewBoundsInside(@Nullable Rect2i parent) {
        return getNewBounds(parent, null);
    }

    public @NotNull Rect2i getNewBounds(@Nullable Rect2i parent, @Nullable Rect2i oldBounds) {
        final var minecraftWindow = Minecraft.getInstance().getWindow();
        final var parentBounds = Objects.nonNull(parent) ? parent : new Rect2i(
                0,
                0,
                minecraftWindow.getGuiScaledWidth(),
                minecraftWindow.getGuiScaledHeight()
        );

        final var newBounds = Objects.isNull(oldBounds) ? new Rect2i(0, 0, 0) : oldBounds.copy();

        newBounds.setWidth(paddingLeft + contentWidth + paddingRight);
        newBounds.setHeight(paddingTop + contentHeight + paddingBottom);

        if (Objects.isNull(oldBounds)) {
            if ((gravity & GRAVITY_RIGHT) >= 1) newBounds.setRight(parentBounds.getRight() - marginRight);
            else if ((gravity & GRAVITY_CENTER_HORIZONTAL) >= 1)
                newBounds.setIpLeft((parentBounds.getWidth() - newBounds.getWidth()) / 2).moveIpX(parentBounds.getX());
            else newBounds.setLeft(parentBounds.getLeft() + marginLeft);

            if ((gravity & GRAVITY_BOTTOM) >= 1) newBounds.setBottom(parentBounds.getBottom() - marginBottom);
            else if ((gravity & GRAVITY_CENTER_VERTICAL) >= 1)
                newBounds.setIpTop((parentBounds.getHeight() - newBounds.getHeight()) / 2).moveIpY(parentBounds.getY());
            else newBounds.setTop(parentBounds.getTop() + marginTop);

            return newBounds;
        }

        if ((gravity & GRAVITY_RIGHT) >= 1)
            newBounds.setLeft(Math.min(
                    newBounds.getLeft() - (newBounds.getWidth() - oldBounds.getWidth()),
                    parentBounds.getRight() - marginRight)
            );
        else if ((gravity & GRAVITY_CENTER_HORIZONTAL) >= 1)
            newBounds.moveIpX((oldBounds.getWidth() - newBounds.getWidth()) / 2);
        else newBounds.setLeft(Math.max(newBounds.getLeft(), parentBounds.getLeft() + marginLeft));

        if ((gravity & GRAVITY_BOTTOM) >= 1)
            newBounds.setTop(Math.min(
                    newBounds.getTop() - (newBounds.getHeight() - oldBounds.getHeight()),
                    parentBounds.getBottom() - marginBottom
            ));
        else if ((gravity & GRAVITY_CENTER_VERTICAL) >= 1)
            newBounds.moveIpY((newBounds.getHeight() - oldBounds.getHeight()) / 2);
        else newBounds.setTop(Math.max(newBounds.getTop(), parentBounds.getTop() + marginTop));

        return newBounds;
    }

    public static @NotNull WidgetPositioner of(@NotNull Rect2i sourceBounds, int padding) {
        return of(sourceBounds, padding, padding, padding, padding);
    }

    public static @NotNull WidgetPositioner of(
            @NotNull Rect2i sourceBounds,
            int paddingLeft,
            int paddingTop,
            int paddingRight,
            int paddingBottom
    ) {
        return new WidgetPositioner(
                sourceBounds.getWidth() - paddingLeft - paddingRight,
                sourceBounds.getHeight() - paddingTop - paddingBottom
        ).setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
    }

    public static @NotNull WidgetPositioner ofMessageContent(@NotNull Component message) {
        final var font = Minecraft.getInstance().font;

        return new WidgetPositioner(font.width(message), font.lineHeight);
    }
}
