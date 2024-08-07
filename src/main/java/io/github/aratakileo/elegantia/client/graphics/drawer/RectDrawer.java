package io.github.aratakileo.elegantia.client.graphics.drawer;

import io.github.aratakileo.elegantia.core.math.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class RectDrawer extends AbstractDrawer<RectDrawer> {
    public RectDrawer(@NotNull GuiGraphics guiGraphics, @NotNull Rect2i bounds) {
        super(guiGraphics, bounds);
    }

    @Override
    public @NotNull RectDrawer withNewBounds(@NotNull Rect2i bounds) {
        return new RectDrawer(guiGraphics, bounds);
    }

    public @NotNull RectDrawer drawRgb(int rgbColor) {
        return draw(rgbColor | 0xff000000);
    }

    public @NotNull RectDrawer draw(int argbColor) {
        if (argbColor != 0x0) {
            guiGraphics.fill(bounds.getLeft(), bounds.getTop(), bounds.getRight(), bounds.getBottom(), argbColor);
            return this;
        }

        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public @NotNull RectDrawer drawRgbGradient(int rgbColorStart, int rgbColorEnd, @NotNull GradientDirection direction) {
        return drawGradient(rgbColorStart | 0xff000000, rgbColorEnd | 0xff000000, direction);
    }

    public @NotNull RectDrawer drawGradient(
            int argbColorStart,
            int argbColorEnd,
            @NotNull GradientDirection direction
    ) {
        if (argbColorStart == 0x0 || argbColorEnd == 0x0) return this;

        return switch (direction) {
            case HORIZONTAL -> drawGradient(argbColorStart, argbColorStart, argbColorEnd, argbColorEnd);
            case VERTICAL -> drawGradient(argbColorStart, argbColorEnd, argbColorEnd, argbColorStart);
            case DIAGONAL -> drawGradient(argbColorEnd, argbColorStart, argbColorEnd, argbColorStart);
            case CORNER_LEFT_TOP -> drawGradient(argbColorStart, argbColorEnd, argbColorEnd, argbColorEnd);
            case CORNER_LEFT_BOTTOM -> drawGradient(argbColorEnd, argbColorStart, argbColorEnd, argbColorEnd);
            case CORNER_RIGHT_BOTTOM -> drawGradient(argbColorEnd, argbColorEnd, argbColorStart, argbColorEnd);
            case CORNER_RIGHT_TOP -> drawGradient(argbColorEnd, argbColorEnd, argbColorEnd, argbColorStart);
        };
    }

    @SuppressWarnings("UnusedReturnValue")
    public @NotNull RectDrawer drawRgbGradient(
            int rgbTopLeftColor,
            int rgbBottomLeftColor,
            int rgbBottomRightColor,
            int rgbTopRightColor
    ) {
        return drawGradient(
                rgbTopLeftColor | 0xff000000,
                rgbBottomLeftColor | 0xff000000,
                rgbBottomRightColor | 0xff000000,
                rgbTopRightColor | 0xff000000
        );
    }

    public @NotNull RectDrawer drawGradient(
            int argbTopLeftColor,
            int argbBottomLeftColor,
            int argbBottomRightColor,
            int argbTopRightColor
    ) {
        if (
                argbTopLeftColor == 0x0
                        || argbBottomLeftColor == 0x0
                        || argbBottomRightColor == 0x0
                        || argbTopRightColor == 0x0
        ) return this;

        final var lastPose = guiGraphics.pose().last();

        guiGraphics.bufferSource().getBuffer(RenderType.gui())
                .addVertex(lastPose, bounds.getLeft(), bounds.getTop(), 0).setColor(argbTopLeftColor)
                .addVertex(lastPose, bounds.getLeft(), bounds.getBottom(), 0).setColor(argbBottomLeftColor)
                .addVertex(lastPose, bounds.getRight(), bounds.getBottom(), 0).setColor(argbBottomRightColor)
                .addVertex(lastPose, bounds.getRight(), bounds.getTop(), 0).setColor(argbTopRightColor);

        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public @NotNull RectDrawer drawRgbStroke(int rgbColor, int thickness) {
        return drawStroke(rgbColor | 0xff000000, thickness);
    }

    public @NotNull RectDrawer drawStroke(int argbColor, int thickness) {
        if (argbColor == 0x0 || thickness == 0) return this;

        guiGraphics.fill(
                bounds.getLeft(),
                bounds.getTop(),
                bounds.getRight(),
                bounds.getTop() + thickness,
                argbColor
        );
        guiGraphics.fill(
                bounds.getLeft(),
                bounds.getBottom() - thickness,
                bounds.getRight(),
                bounds.getBottom(),
                argbColor
        );
        guiGraphics.fill(
                bounds.getLeft(),
                bounds.getTop() + thickness,
                bounds.getLeft() + thickness,
                bounds.getBottom() - thickness,
                argbColor
        );
        guiGraphics.fill(
                bounds.getRight() - thickness,
                bounds.getTop() + thickness,
                bounds.getRight(),
                bounds.getBottom() - thickness,
                argbColor
        );

        return this;
    }

    public @NotNull TextureDrawer asTextureDrawer(@NotNull ResourceLocation texture) {
        return new TextureDrawer(texture, TextureDrawer.getTextureSize(texture), bounds, guiGraphics);
    }

    public @NotNull TextureDrawer asTextureDrawer(@NotNull ResourceLocation texture, @NotNull Size2iInterface textureSize) {
        return new TextureDrawer(texture, textureSize, bounds, guiGraphics);
    }

    public @NotNull TextureDrawer textureDrawer(@NotNull ResourceLocation texture, @NotNull Rect2i bounds) {
        return new TextureDrawer(texture, TextureDrawer.getTextureSize(texture), bounds, guiGraphics);
    }

    public @NotNull TextureDrawer textureDrawer(
            @NotNull ResourceLocation texture,
            @NotNull Size2iInterface textureSize,
            @NotNull Rect2i bounds
    ) {
        return new TextureDrawer(texture, textureSize, bounds, guiGraphics);
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

    public static RectDrawer of(
            @NotNull GuiGraphics guiGraphics,
            @NotNull Vector2iInterface pos,
            @NotNull Size2iInterface size
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

    public static RectDrawer of(@NotNull GuiGraphics guiGraphics, int x, int y, @NotNull Size2iInterface size) {
        return new RectDrawer(guiGraphics, Rect2i.of(x, y, size));
    }

    public static RectDrawer of(@NotNull GuiGraphics guiGraphics, int x, int y, int width, int height) {
        return new RectDrawer(guiGraphics, new Rect2i(x, y, width, height));
    }

    public static RectDrawer ofSquare(@NotNull GuiGraphics guiGraphics, @NotNull Vector2iInterface pos, int size) {
        return new RectDrawer(guiGraphics, Rect2i.ofSquare(pos, size));
    }

    public static RectDrawer ofSquare(@NotNull GuiGraphics guiGraphics, int x, int y, int size) {
        return new RectDrawer(guiGraphics, Rect2i.ofSquare(x, y, size));
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
