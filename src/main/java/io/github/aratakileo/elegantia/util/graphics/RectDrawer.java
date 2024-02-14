package io.github.aratakileo.elegantia.util.graphics;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.aratakileo.elegantia.math.Rect2i;
import io.github.aratakileo.elegantia.math.Vector2iInterface;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

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

    public @NotNull RectDrawer draw(int color) {
        guiGraphics.fill(x, y, x + width, y + height, color);
        return this;
    }

    public @NotNull RectDrawer drawStroke(int color, int thickness) {
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

    public @NotNull RectDrawer renderTexture(@NotNull ResourceLocation resourceLocation) {
        RenderSystem.enableBlend();
        guiGraphics.blit(resourceLocation, x, y, 0f, 0f, width, height, width, height);
        RenderSystem.disableBlend();

        return this;
    }

    public @NotNull RectDrawer renderFittedCenterTexture(
            @NotNull ResourceLocation resourceLocation,
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
                resourceLocation,
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
}
