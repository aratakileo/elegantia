package io.github.aratakileo.elegantia.client.gui;

import io.github.aratakileo.elegantia.client.gui.widget.AbstractWidget;
import io.github.aratakileo.elegantia.core.math.Rect2i;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

public class WidgetBuilder {
    public static final int GRAVITY_LEFT = 0,
            GRAVITY_TOP = 0,
            GRAVITY_RIGHT = 1,
            GRAVITY_BOTTOM = 1 << 1,
            GRAVITY_CENTER_HORIZONTAL = 1 << 2,
            GRAVITY_CENTER_VERTICAL = 1 << 3,
            GRAVITY_CENTER = GRAVITY_CENTER_HORIZONTAL | GRAVITY_CENTER_VERTICAL;

    private final Dimension contentSize;
    private int paddingLeft = 0, paddingTop = 0, paddingRight = 0, paddingBottom = 0;
    private int marginLeft = 0, marginTop = 0, marginRight = 0, marginBottom = 0;
    private int gravity = GRAVITY_LEFT | GRAVITY_TOP;
    private @Nullable Rect2i initialBounds, parentBounds;

    public WidgetBuilder(int contentSize) {
        this.contentSize = new Dimension(contentSize, contentSize);
    }

    public WidgetBuilder(@NotNull Dimension contentSize) {
        this.contentSize = contentSize;
    }

    public WidgetBuilder(int contentWidth, int contentHeight) {
        contentSize = new Dimension(contentWidth, contentHeight);
    }

    public @NotNull WidgetBuilder setPadding(int padding) {
        paddingLeft = padding;
        paddingTop = padding;
        paddingRight = padding;
        paddingBottom = padding;

        return this;
    }

    public @NotNull WidgetBuilder setPadding(
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

    public @NotNull WidgetBuilder setPaddingLeft(int paddingLeft) {
        this.paddingLeft = paddingLeft;
        return this;
    }

    public @NotNull WidgetBuilder setPaddingTop(int paddingTop) {
        this.paddingTop = paddingTop;
        return this;
    }

    public @NotNull WidgetBuilder setPaddingRight(int paddingRight) {
        this.paddingRight = paddingRight;
        return this;
    }

    public @NotNull WidgetBuilder setPaddingBottom(int paddingBottom) {
        this.paddingBottom = paddingBottom;
        return this;
    }

    public @NotNull WidgetBuilder setMargin(int margin) {
        marginLeft = margin;
        marginTop = margin;
        marginRight = margin;
        marginBottom = margin;
        return this;
    }

    public @NotNull WidgetBuilder setMargin(int marginLeft, int marginTop, int marginRight, int marginBottom) {
        this.marginLeft = marginLeft;
        this.marginTop = marginTop;
        this.marginRight = marginRight;
        this.marginBottom = marginBottom;
        return this;
    }

    public @NotNull WidgetBuilder setMarginLeft(int marginLeft) {
        this.marginLeft = marginLeft;
        return this;
    }

    public @NotNull WidgetBuilder setMarginTop(int marginTop) {
        this.marginTop = marginTop;
        return this;
    }

    public @NotNull WidgetBuilder setMarginRight(int marginRight) {
        this.marginRight = marginRight;
        return this;
    }

    public @NotNull WidgetBuilder setMarginBottom(int marginBottom) {
        this.marginBottom = marginBottom;
        return this;
    }

    public @NotNull WidgetBuilder setGravity(int gravity) {
        this.gravity = gravity;
        return this;
    }

    public @NotNull WidgetBuilder setInitialBounds(@Nullable Rect2i initialBounds) {
        this.initialBounds = initialBounds;
        return this;
    }

    public @NotNull WidgetBuilder setParentBounds(@Nullable Rect2i parentBounds) {
        this.parentBounds = parentBounds;
        return this;
    }

    public @NotNull Rect2i buildBounds() {
        final var minecraftWindow = Minecraft.getInstance().getWindow();
        final var canvasBounds = Objects.nonNull(parentBounds) ? parentBounds : Rect2i.startPosition(
                minecraftWindow.getGuiScaledWidth(),
                minecraftWindow.getGuiScaledHeight()
        );

        final var newBounds = Objects.isNull(initialBounds) ? Rect2i.empty() : initialBounds.copy();

        newBounds.setSize(
                paddingLeft + contentSize.width + paddingRight,
                paddingTop + contentSize.height + paddingBottom
        );

        if (Objects.isNull(initialBounds)) {
            if ((gravity & GRAVITY_RIGHT) >= 1)
                newBounds.setRight(canvasBounds.getRight() - marginRight);
            else if ((gravity & GRAVITY_CENTER_HORIZONTAL) >= 1)
                newBounds.setLeft((canvasBounds.width - newBounds.width) / 2).moveX(canvasBounds.x);
            else newBounds.setLeft(canvasBounds.getLeft() + marginLeft);

            if ((gravity & GRAVITY_BOTTOM) >= 1) newBounds.setBottom(canvasBounds.getBottom() - marginBottom);
            else if ((gravity & GRAVITY_CENTER_VERTICAL) >= 1)
                newBounds.setTop((canvasBounds.height - newBounds.height) / 2).moveY(canvasBounds.y);
            else newBounds.setTop(canvasBounds.getTop() + marginTop);

            return newBounds;
        }

        if ((gravity & GRAVITY_RIGHT) >= 1)
            newBounds.setLeft(Math.min(
                    newBounds.getLeft() - (newBounds.width - initialBounds.width),
                    canvasBounds.getRight() - marginRight)
            );
        else if ((gravity & GRAVITY_CENTER_HORIZONTAL) >= 1)
            newBounds.moveX((initialBounds.width - newBounds.width) / 2);
        else newBounds.setLeft(Math.max(newBounds.getLeft(), canvasBounds.getLeft() + marginLeft));

        if ((gravity & GRAVITY_BOTTOM) >= 1)
            newBounds.setTop(Math.min(
                    newBounds.getTop() - (newBounds.height - initialBounds.height),
                    canvasBounds.getBottom() - marginBottom
            ));
        else if ((gravity & GRAVITY_CENTER_VERTICAL) >= 1)
            newBounds.moveY((newBounds.height - initialBounds.height) / 2);
        else newBounds.setTop(Math.max(newBounds.getTop(), canvasBounds.getTop() + marginTop));

        return newBounds;
    }

    public <T> @NotNull T buildWidget(@NotNull Function<Rect2i, T> initializer) {
        return initializer.apply(buildBounds());
    }

    public <T> @NotNull T buildWidget(@NotNull Component message, @NotNull BiFunction<Rect2i, Component, T> initializer) {
        return initializer.apply(buildBounds(), message);
    }

    @SuppressWarnings("UnusedReturnValue")
    public <T extends AbstractWidget> @NotNull T applyBounds(T widget) {
        widget.setBounds(buildBounds());
        return widget;
    }

    public static @NotNull WidgetBuilder of(@NotNull WidgetBuilder widgetBuilder) {
        return of(
                widgetBuilder.buildBounds(),
                widgetBuilder.paddingLeft,
                widgetBuilder.paddingTop,
                widgetBuilder.paddingRight,
                widgetBuilder.paddingBottom
        ).setMargin(
                widgetBuilder.marginLeft,
                widgetBuilder.marginTop,
                widgetBuilder.marginRight,
                widgetBuilder.marginBottom
        ).setGravity(widgetBuilder.gravity).setParentBounds(widgetBuilder.parentBounds);
    }

    public static @NotNull WidgetBuilder of(@NotNull Rect2i initialBounds, int padding) {
        return of(initialBounds, padding, padding, padding, padding);
    }

    public static @NotNull WidgetBuilder of(
            @NotNull Rect2i initialBounds,
            int paddingLeft,
            int paddingTop,
            int paddingRight,
            int paddingBottom
    ) {
        return new WidgetBuilder(
                initialBounds.width - paddingLeft - paddingRight,
                initialBounds.height - paddingTop - paddingBottom
        ).setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
    }

    public static @NotNull WidgetBuilder about(@NotNull Component message) {
        final var font = Minecraft.getInstance().font;

        return new WidgetBuilder(font.width(message), font.lineHeight);
    }
}
