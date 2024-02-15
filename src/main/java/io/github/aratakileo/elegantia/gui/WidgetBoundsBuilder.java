package io.github.aratakileo.elegantia.gui;

import io.github.aratakileo.elegantia.math.Rect2i;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class WidgetBoundsBuilder {
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
    private @Nullable Rect2i initialBounds, parentBounds;

    public WidgetBoundsBuilder(int contentSize) {
        this.contentWidth = contentSize;
        this.contentHeight = contentSize;
    }

    public WidgetBoundsBuilder(int contentWidth, int contentHeight) {
        this.contentWidth = contentWidth;
        this.contentHeight = contentHeight;
    }

    public @NotNull WidgetBoundsBuilder setPadding(int padding) {
        paddingLeft = padding;
        paddingTop = padding;
        paddingRight = padding;
        paddingBottom = padding;

        return this;
    }

    public @NotNull WidgetBoundsBuilder setPadding(
            int paddingLeft,
            int paddingTop,
            int paddingRight,
            int paddingBottom
    ) {
        this.paddingLeft = paddingLeft;
        this.paddingTop = paddingTop;
        this.paddingRight = paddingRight;
        this.paddingBottom = paddingBottom;

        return this;
    }

    public @NotNull WidgetBoundsBuilder setPaddingLeft(int paddingLeft) {
        this.paddingLeft = paddingLeft;
        return this;
    }

    public @NotNull WidgetBoundsBuilder setPaddingTop(int paddingTop) {
        this.paddingTop = paddingTop;
        return this;
    }

    public @NotNull WidgetBoundsBuilder setPaddingRight(int paddingRight) {
        this.paddingRight = paddingRight;
        return this;
    }

    public @NotNull WidgetBoundsBuilder setPaddingBottom(int paddingBottom) {
        this.paddingBottom = paddingBottom;
        return this;
    }

    public @NotNull WidgetBoundsBuilder setMargin(int margin) {
        marginLeft = margin;
        marginTop = margin;
        marginRight = margin;
        marginBottom = margin;
        return this;
    }

    public @NotNull WidgetBoundsBuilder setMargin(int marginLeft, int marginTop, int marginRight, int marginBottom) {
        this.marginLeft = marginLeft;
        this.marginTop = marginTop;
        this.marginRight = marginRight;
        this.marginBottom = marginBottom;
        return this;
    }

    public @NotNull WidgetBoundsBuilder setMarginLeft(int marginLeft) {
        this.marginLeft = marginLeft;
        return this;
    }

    public @NotNull WidgetBoundsBuilder setMarginTop(int marginTop) {
        this.marginTop = marginTop;
        return this;
    }

    public @NotNull WidgetBoundsBuilder setMarginRight(int marginRight) {
        this.marginRight = marginRight;
        return this;
    }

    public @NotNull WidgetBoundsBuilder setMarginBottom(int marginBottom) {
        this.marginBottom = marginBottom;
        return this;
    }

    public @NotNull WidgetBoundsBuilder setGravity(int gravity) {
        this.gravity = gravity;
        return this;
    }

    public @NotNull WidgetBoundsBuilder setInitialBounds(@Nullable Rect2i initialBounds) {
        this.initialBounds = initialBounds;
        return this;
    }

    public @NotNull WidgetBoundsBuilder setParentBounds(@Nullable Rect2i parentBounds) {
        this.parentBounds = parentBounds;
        return this;
    }

    public @NotNull Rect2i build() {
        final var minecraftWindow = Minecraft.getInstance().getWindow();
        final var canvasBounds = Objects.nonNull(parentBounds) ? parentBounds : new Rect2i(
                0,
                0,
                minecraftWindow.getGuiScaledWidth(),
                minecraftWindow.getGuiScaledHeight()
        );

        final var newBounds = Objects.isNull(initialBounds) ? new Rect2i(0, 0, 0) : initialBounds.copy();

        newBounds.setWidth(paddingLeft + contentWidth + paddingRight);
        newBounds.setHeight(paddingTop + contentHeight + paddingBottom);

        if (Objects.isNull(initialBounds)) {
            if ((gravity & GRAVITY_RIGHT) >= 1) newBounds.setRight(canvasBounds.getRight() - marginRight);
            else if ((gravity & GRAVITY_CENTER_HORIZONTAL) >= 1)
                newBounds.setIpLeft((canvasBounds.getWidth() - newBounds.getWidth()) / 2).moveIpX(canvasBounds.getX());
            else newBounds.setLeft(canvasBounds.getLeft() + marginLeft);

            if ((gravity & GRAVITY_BOTTOM) >= 1) newBounds.setBottom(canvasBounds.getBottom() - marginBottom);
            else if ((gravity & GRAVITY_CENTER_VERTICAL) >= 1)
                newBounds.setIpTop((canvasBounds.getHeight() - newBounds.getHeight()) / 2).moveIpY(canvasBounds.getY());
            else newBounds.setTop(canvasBounds.getTop() + marginTop);

            return newBounds;
        }

        if ((gravity & GRAVITY_RIGHT) >= 1)
            newBounds.setLeft(Math.min(
                    newBounds.getLeft() - (newBounds.getWidth() - initialBounds.getWidth()),
                    canvasBounds.getRight() - marginRight)
            );
        else if ((gravity & GRAVITY_CENTER_HORIZONTAL) >= 1)
            newBounds.moveIpX((initialBounds.getWidth() - newBounds.getWidth()) / 2);
        else newBounds.setLeft(Math.max(newBounds.getLeft(), canvasBounds.getLeft() + marginLeft));

        if ((gravity & GRAVITY_BOTTOM) >= 1)
            newBounds.setTop(Math.min(
                    newBounds.getTop() - (newBounds.getHeight() - initialBounds.getHeight()),
                    canvasBounds.getBottom() - marginBottom
            ));
        else if ((gravity & GRAVITY_CENTER_VERTICAL) >= 1)
            newBounds.moveIpY((newBounds.getHeight() - initialBounds.getHeight()) / 2);
        else newBounds.setTop(Math.max(newBounds.getTop(), canvasBounds.getTop() + marginTop));

        return newBounds;
    }

    public static @NotNull WidgetBoundsBuilder of(@NotNull WidgetBoundsBuilder widgetBoundsBuilder) {
        return of(
                widgetBoundsBuilder.build(),
                widgetBoundsBuilder.paddingLeft,
                widgetBoundsBuilder.paddingTop,
                widgetBoundsBuilder.paddingRight,
                widgetBoundsBuilder.paddingBottom
        ).setMargin(
                widgetBoundsBuilder.marginLeft,
                widgetBoundsBuilder.marginTop,
                widgetBoundsBuilder.marginRight,
                widgetBoundsBuilder.marginBottom
        ).setGravity(widgetBoundsBuilder.gravity).setParentBounds(widgetBoundsBuilder.parentBounds);
    }

    public static @NotNull WidgetBoundsBuilder of(@NotNull Rect2i initialBounds, int padding) {
        return of(initialBounds, padding, padding, padding, padding);
    }

    public static @NotNull WidgetBoundsBuilder of(
            @NotNull Rect2i initialBounds,
            int paddingLeft,
            int paddingTop,
            int paddingRight,
            int paddingBottom
    ) {
        return new WidgetBoundsBuilder(
                initialBounds.getWidth() - paddingLeft - paddingRight,
                initialBounds.getHeight() - paddingTop - paddingBottom
        ).setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
    }

    public static @NotNull WidgetBoundsBuilder ofMessageContent(@NotNull Component message) {
        final var font = Minecraft.getInstance().font;

        return new WidgetBoundsBuilder(font.width(message), font.lineHeight);
    }
}
