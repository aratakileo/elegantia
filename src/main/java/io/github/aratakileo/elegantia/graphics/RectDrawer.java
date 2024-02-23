package io.github.aratakileo.elegantia.graphics;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.aratakileo.elegantia.math.Rect2i;
import io.github.aratakileo.elegantia.math.Vector2iInterface;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class RectDrawer {
    private final GuiGraphics guiGraphics;
    public final Rect2i bounds;

    private RectDrawer(@NotNull GuiGraphics guiGraphics, @NotNull Rect2i bounds) {
        this.guiGraphics = guiGraphics;
        this.bounds = bounds;
    }

    public @NotNull GuiGraphics getGuiGraphics() {
        return guiGraphics;
    }

    public @NotNull RectDrawer draw(int color) {
        if (color != 0x0) {
            guiGraphics.fill(bounds.getX(), bounds.getY(), bounds.getRight(), bounds.getBottom(), color);
            return this;
        }

        return this;
    }

    public @NotNull RectDrawer drawGradient(int colorStart, int colorEnd, @NotNull GradientDirection direction) {
        if (colorStart == 0x0 || colorEnd == 0x0) return this;

        final var buffer = new RectVertexConsumer(
                bounds,
                guiGraphics.bufferSource().getBuffer(RenderType.gui()),
                guiGraphics.pose().last().pose()
        );

        switch (direction) {
            case HORIZONTAL -> {
                buffer.coloredVertex(0, colorStart);
                buffer.coloredVertex(1, colorStart);
                buffer.coloredVertex(2, colorEnd);
                buffer.coloredVertex(3, colorEnd);
            }
            case VERTICAL -> {
                buffer.coloredVertex(0, colorStart);
                buffer.coloredVertex(1, colorEnd);
                buffer.coloredVertex(2, colorEnd);
                buffer.coloredVertex(3, colorStart);
            }
            case DIAGONAL -> {
                buffer.coloredVertex(0, colorEnd);
                buffer.coloredVertex(1, colorStart);
                buffer.coloredVertex(2, colorEnd);
                buffer.coloredVertex(3, colorStart);
            }
            case CORNER_LEFT_TOP -> {
                buffer.coloredVertex(0, colorStart);
                buffer.coloredVertex(1, colorEnd);
                buffer.coloredVertex(2, colorEnd);
                buffer.coloredVertex(3, colorEnd);
            }
            case CORNER_LEFT_BOTTOM -> {
                buffer.coloredVertex(0, colorEnd);
                buffer.coloredVertex(1, colorStart);
                buffer.coloredVertex(2, colorEnd);
                buffer.coloredVertex(3, colorEnd);
            }
            case CORNER_RIGHT_BOTTOM -> {
                buffer.coloredVertex(0, colorEnd);
                buffer.coloredVertex(1, colorEnd);
                buffer.coloredVertex(2, colorStart);
                buffer.coloredVertex(3, colorEnd);
            }
            case CORNER_RIGHT_TOP -> {
                buffer.coloredVertex(0, colorEnd);
                buffer.coloredVertex(1, colorEnd);
                buffer.coloredVertex(2, colorEnd);
                buffer.coloredVertex(3, colorStart);
            }
        }

        return this;
    }

    public @NotNull RectDrawer drawStroke(int color, int thickness) {
        if (color == 0x0 || thickness == 0) return this;

        guiGraphics.fill(bounds.getX(), bounds.getY(), bounds.getRight(), bounds.getY() + thickness, color);
        guiGraphics.fill(bounds.getX(), bounds.getBottom() - thickness, bounds.getRight(), bounds.getBottom(), color);

        guiGraphics.fill(
                bounds.getX(),
                bounds.getY() + thickness,
                bounds.getX() + thickness,
                bounds.getBottom() - thickness,
                color
        );
        guiGraphics.fill(
                bounds.getRight() - thickness,
                bounds.getY() + thickness,
                bounds.getRight(),
                bounds.getBottom() - thickness,
                color
        );

        return this;
    }

    public @NotNull RectDrawer renderTexture(@NotNull ResourceLocation texture) {
        RenderSystem.enableBlend();
        guiGraphics.blit(
                texture,
                bounds.getX(),
                bounds.getY(),
                0f,
                0f,
                bounds.getWidth(),
                bounds.getHeight(),
                bounds.getWidth(),
                bounds.getHeight()
        );
        RenderSystem.disableBlend();

        return this;
    }

    public @NotNull RectDrawer renderFittedCenterTexture(@NotNull ResourceLocation texture) {
        try {
            final var nativeImage = NativeImage.read(
                    Minecraft.getInstance().getResourceManager().open(texture)
            );

            return renderFittedCenterTexture(texture, nativeImage.getWidth(), nativeImage.getHeight());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public @NotNull RectDrawer renderFittedCenterTexture(
            @NotNull ResourceLocation texture,
            int textureWidth,
            int textureHeight
    ) {
        int renderWidth = bounds.getWidth(),
                renderHeight = bounds.getHeight(),
                renderX = bounds.getX(),
                renderY = bounds.getY();

        if (textureWidth < textureHeight) {
            final var oldRenderWidth = renderWidth;

            renderWidth *= ((float) textureWidth / textureHeight);
            renderX += (oldRenderWidth - renderWidth) / 2;
        } else if (textureHeight < textureWidth) {
            final var oldRenderHeight = renderHeight;

            renderHeight *= ((float) textureHeight / textureWidth);
            renderY += (oldRenderHeight - renderHeight) / 2;
        }

        RenderSystem.enableBlend();
        guiGraphics.blit(
                texture,
                renderX,
                renderY,
                0f,
                0f,
                renderWidth,
                renderHeight,
                renderWidth,
                renderHeight
        );
        RenderSystem.disableBlend();
        return this;
    }

    @Override
    public String toString() {
        return "RectDrawer{%s, %s, %s, %s}".formatted(
                bounds.getX(),
                bounds.getY(),
                bounds.getWidth(),
                bounds.getHeight()
        );
    }

    public static @NotNull RectDrawer with(@NotNull GuiGraphics guiGraphics, @NotNull Rect2i rect2i) {
        return new RectDrawer(guiGraphics, rect2i.copy());
    }

    public static RectDrawer with(@NotNull GuiGraphics guiGraphics, @NotNull Vector2iInterface pos, int size) {
        return new RectDrawer(guiGraphics, new Rect2i(pos, size));
    }

    public static RectDrawer with(
            @NotNull GuiGraphics guiGraphics,
            @NotNull Vector2iInterface pos,
            int width,
            int height
    ) {
        return new RectDrawer(guiGraphics, new Rect2i(pos, width, height));
    }

    public static RectDrawer with(@NotNull GuiGraphics guiGraphics, int x, int y, int size) {
        return new RectDrawer(guiGraphics, new Rect2i(x, y, size));
    }

    public static RectDrawer with(@NotNull GuiGraphics guiGraphics, int x, int y, int width, int height) {
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
