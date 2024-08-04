package io.github.aratakileo.elegantia.graphics.drawer;

import io.github.aratakileo.elegantia.math.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class RectDrawer {
    public final GuiGraphics guiGraphics;
    public final Rect2i bounds;

    public RectDrawer(@NotNull GuiGraphics guiGraphics, @NotNull Rect2i bounds) {
        this.guiGraphics = guiGraphics;
        this.bounds = bounds;
    }

    public @NotNull RectDrawer draw(int color) {
        if (color != 0x0) {
            guiGraphics.fill(bounds.getLeft(), bounds.getTop(), bounds.getRight(), bounds.getBottom(), color);
            return this;
        }

        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public @NotNull RectDrawer drawGradient(int colorStart, int colorEnd, @NotNull GradientDirection direction) {
        if (colorStart == 0x0 || colorEnd == 0x0) return this;

        return switch (direction) {
            case HORIZONTAL -> drawGradient(colorStart, colorStart, colorEnd, colorEnd);
            case VERTICAL -> drawGradient(colorStart, colorEnd, colorEnd, colorStart);
            case DIAGONAL -> drawGradient(colorEnd, colorStart, colorEnd, colorStart);
            case CORNER_LEFT_TOP -> drawGradient(colorStart, colorEnd, colorEnd, colorEnd);
            case CORNER_LEFT_BOTTOM -> drawGradient(colorEnd, colorStart, colorEnd, colorEnd);
            case CORNER_RIGHT_BOTTOM -> drawGradient(colorEnd, colorEnd, colorStart, colorEnd);
            case CORNER_RIGHT_TOP -> drawGradient(colorEnd, colorEnd, colorEnd, colorStart);
        };
    }

    @SuppressWarnings("UnusedReturnValue")
    public @NotNull RectDrawer drawGradient(int topLeftColor, int bottomLeftColor, int bottomRightColor, int topRightColor) {
        if (topLeftColor == 0x0 || bottomLeftColor == 0x0 || bottomRightColor == 0x0 || topRightColor == 0x0)
            return this;

        final var lastPose = guiGraphics.pose().last();

        guiGraphics.bufferSource().getBuffer(RenderType.gui())
                .addVertex(lastPose, bounds.getLeft(), bounds.getTop(), 0).setColor(topLeftColor)
                .addVertex(lastPose, bounds.getLeft(), bounds.getBottom(), 0).setColor(bottomLeftColor)
                .addVertex(lastPose, bounds.getRight(), bounds.getBottom(), 0).setColor(bottomRightColor)
                .addVertex(lastPose, bounds.getRight(), bounds.getTop(), 0).setColor(topRightColor);

        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public @NotNull RectDrawer drawStroke(int color, int thickness) {
        if (color == 0x0 || thickness == 0) return this;

        guiGraphics.fill(bounds.getLeft(), bounds.getTop(), bounds.getRight(), bounds.getTop() + thickness, color);
        guiGraphics.fill(bounds.getLeft(), bounds.getBottom() - thickness, bounds.getRight(), bounds.getBottom(), color);

        guiGraphics.fill(
                bounds.getLeft(),
                bounds.getTop() + thickness,
                bounds.getLeft() + thickness,
                bounds.getBottom() - thickness,
                color
        );
        guiGraphics.fill(
                bounds.getRight() - thickness,
                bounds.getTop() + thickness,
                bounds.getRight(),
                bounds.getBottom() - thickness,
                color
        );

        return this;
    }

    public @NotNull TextureDrawer texture(@NotNull ResourceLocation texture, @NotNull Dimension textureSize) {
        return new TextureDrawer(texture, textureSize, bounds, guiGraphics);
    }

    public @NotNull TextureDrawer texture(@NotNull ResourceLocation texture) {
        return new TextureDrawer(texture, TextureDrawer.getTextureSize(texture), bounds, guiGraphics);
    }

    @Override
    public String toString() {
        return "RectDrawer{%s, %s, %s, %s}".formatted(
                bounds.x,
                bounds.y,
                bounds.width,
                bounds.height
        );
    }

    public static RectDrawer of(@NotNull GuiGraphics guiGraphics, @NotNull Vector2iInterface pos, int size) {
        return new RectDrawer(guiGraphics, Rect2i.of(pos, size));
    }

    public static RectDrawer of(
            @NotNull GuiGraphics guiGraphics,
            @NotNull Vector2iInterface pos,
            @NotNull Dimension size
    ) {
        return new RectDrawer(guiGraphics, Rect2i.of(pos, size));
    }

    public static RectDrawer of(
            @NotNull GuiGraphics guiGraphics,
            @NotNull Vector2iInterface pos,
            int width,
            int height
    ) {
        return new RectDrawer(guiGraphics, Rect2i.of(pos, width, height));
    }

    public static RectDrawer of(@NotNull GuiGraphics guiGraphics, int x, int y, int size) {
        return new RectDrawer(guiGraphics, Rect2i.of(x, y, size));
    }

    public static RectDrawer of(@NotNull GuiGraphics guiGraphics, int x, int y, @NotNull Dimension size) {
        return new RectDrawer(guiGraphics, Rect2i.of(x, y, size));
    }

    public static RectDrawer of(@NotNull GuiGraphics guiGraphics, int x, int y, int width, int height) {
        return new RectDrawer(guiGraphics, new Rect2i(x, y, width, height));
    }

    public enum GradientDirection {
        HORIZONTAL,
        VERTICAL,
        DIAGONAL,
        CORNER_LEFT_TOP,
        CORNER_LEFT_BOTTOM,
        CORNER_RIGHT_BOTTOM,
        CORNER_RIGHT_TOP
    }
}