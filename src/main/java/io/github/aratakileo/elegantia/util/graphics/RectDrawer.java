package io.github.aratakileo.elegantia.util.graphics;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.aratakileo.elegantia.math.Rect2i;
import io.github.aratakileo.elegantia.math.Vector2iInterface;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class RectDrawer {
    private final GuiGraphics guiGraphics;
    private int x, y, width, height;

    public RectDrawer(@NotNull GuiGraphics guiGraphics, @NotNull Rect2i bounds) {
        this(guiGraphics, bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
    }

    public RectDrawer(@NotNull GuiGraphics guiGraphics, @NotNull Vector2iInterface pos, int width, int height) {
        this(guiGraphics, pos.x(), pos.y(), width, height);
    }

    public RectDrawer(@NotNull GuiGraphics guiGraphics, int x, int y, int width, int height) {
        this.guiGraphics = guiGraphics;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public @NotNull GuiGraphics getGuiGraphics() {
        return guiGraphics;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public @NotNull Rect2i getBounds() {
        return new Rect2i(x, y, width, height);
    }

    public @NotNull RectDrawer draw(int color) {
        if (color != 0x0) {
            guiGraphics.fill(x, y, x + width, y + height, color);
            return this;
        }

        return this;
    }

    public @NotNull RectDrawer drawGradient(int colorStart, int colorEnd) {
        if (colorStart != 0x0 && colorEnd != 0x0) {
            guiGraphics.fillGradient(x, y, x + width, y + height, colorStart, colorEnd);
            return this;
        }

        return this;
    }

    public @NotNull RectDrawer drawStroke(int color, int thickness) {
        if (color == 0x0 || thickness == 0) return this;

        guiGraphics.fill(x, y, x + width, y + thickness, color);
        guiGraphics.fill(x, y + height - thickness, x + width, y + height, color);

        guiGraphics.fill(
                x,
                y + thickness,
                x + thickness,
                y + height - thickness,
                color
        );
        guiGraphics.fill(
                x + width - thickness,
                y + thickness,
                x + width,
                y + height - thickness,
                color
        );

        return this;
    }

    public @NotNull RectDrawer renderTexture(@NotNull ResourceLocation texture) {
        RenderSystem.enableBlend();
        guiGraphics.blit(texture, x, y, 0f, 0f, width, height, width, height);
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
        int renderWidth = width, renderHeight = height, renderX = x, renderY = y;

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

    public @NotNull RectDrawer redefineBounds(@NotNull Rect2i bounds) {
        return redefineBounds(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
    }

    public @NotNull RectDrawer redefineBounds(@NotNull Vector2iInterface pos, int width, int height) {
        return redefineBounds(pos.x(), pos.y(), width, height);
    }

    public @NotNull RectDrawer redefineBounds(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        return this;
    }

    @Override
    public String toString() {
        return "RectDrawer{%s, %s, %s, %s}".formatted(x, y, width, height);
    }
}
