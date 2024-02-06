package io.github.aratakileo.elegantia.util;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.aratakileo.elegantia.gui.widget.AbstractWidget;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class GuiGraphicsUtil {
    public static void drawRect(@NotNull GuiGraphics guiGraphics, @NotNull Rect2i bounds, int color) {
        drawRect(guiGraphics, bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight(), color);
    }

    public static void drawRect(@NotNull GuiGraphics guiGraphics, int x, int y, int width, int height, int color) {
        guiGraphics.fill(x, y, x + width, y + height, color);
    }

    public static void drawRectStroke(
            @NotNull GuiGraphics guiGraphics,
            @NotNull Rect2i bounds,
            int strokeThickness,
            int strokeColor
    ) {
        drawRectStroke(
                guiGraphics,
                bounds.getX(),
                bounds.getY(),
                bounds.getWidth(),
                bounds.getHeight(),
                strokeThickness,
                strokeColor
        );
    }

    public static void drawRectStroke(
            @NotNull GuiGraphics guiGraphics,
            int x,
            int y,
            int width,
            int height,
            int strokeThickness,
            int strokeColor
    ) {
        guiGraphics.fill(x, y, x + width, y + strokeThickness, strokeColor);
        guiGraphics.fill(x, y + height - strokeThickness, x + width, y + height, strokeColor);

        guiGraphics.fill(
                x,
                y + strokeThickness,
                x + strokeThickness,
                y + height - strokeThickness,
                strokeColor
        );
        guiGraphics.fill(
                x + width - strokeThickness,
                y + strokeThickness,
                x + width,
                y + height - strokeThickness,
                strokeColor
        );
    }

    public static void drawRect(
            @NotNull GuiGraphics guiGraphics,
            @NotNull Rect2i bounds,
            int color,
            int strokeThickness,
            int strokeColor
    ) {
        drawRect(guiGraphics, bounds, color);
        drawRectStroke(guiGraphics, bounds, strokeThickness, strokeColor);
    }

    public static void drawRect(
            @NotNull GuiGraphics guiGraphics,
            int x,
            int y,
            int width,
            int height,
            int color,
            int strokeThickness,
            int strokeColor
    ) {
        drawRect(guiGraphics, x, y, width, height, color);
        drawRectStroke(guiGraphics, x, y, width, height, strokeThickness, strokeColor);
    }

    public static void renderTexture(
            @NotNull GuiGraphics guiGraphics,
            @NotNull ResourceLocation resourceLocation,
            @NotNull Rect2i textureBounds
    ) {
        renderTexture(
                guiGraphics,
                resourceLocation,
                textureBounds.getX(),
                textureBounds.getY(),
                textureBounds.getWidth(),
                textureBounds.getHeight()
        );
    }

    public static void renderTexture(
            @NotNull GuiGraphics guiGraphics,
            @NotNull ResourceLocation resourceLocation,
            int x,
            int y,
            int width,
            int height
    ) {
        RenderSystem.enableBlend();
        guiGraphics.blit(resourceLocation, x, y, 0f, 0f, width, height, width, height);
        RenderSystem.disableBlend();
    }

    public static void renderFittedCenterTexture(
            @NotNull GuiGraphics guiGraphics,
            @NotNull ResourceLocation resourceLocation,
            @NotNull Rect2i textureBounds,
            int textureWidth,
            int textureHeight
    ) {
        renderFittedCenterTexture(
                guiGraphics,
                resourceLocation,
                textureBounds.getX(),
                textureBounds.getY(),
                textureBounds.getWidth(),
                textureBounds.getHeight(),
                textureWidth,
                textureHeight
        );
    }

    public static void renderFittedCenterTexture(
            @NotNull GuiGraphics guiGraphics,
            @NotNull ResourceLocation resourceLocation,
            int x,
            int y,
            int renderWidth,
            int renderHeight,
            int textureWidth,
            int textureHeight
    ) {
        if (textureWidth < textureHeight) {
            final var oldRenderWidth = renderWidth;

            renderWidth *= ((float) textureWidth / textureHeight);
            x += (oldRenderWidth - renderWidth) / 2;
        } else if (textureHeight < textureWidth) {
            final var oldRenderHeight = renderHeight;

            renderHeight *= ((float) textureHeight / textureWidth);
            y += (oldRenderHeight - renderHeight) / 2;
        }

        renderTexture(guiGraphics, resourceLocation, x, y, renderWidth, renderHeight);
    }

    public static void renderScrollingText(
            @NotNull GuiGraphics guiGraphics,
            @NotNull Component text,
            @NotNull Rect2i bounds,
            int color
    ) {
        final var font = Minecraft.getInstance().font;
        final var textWidth = font.width(text);
        final var y = (bounds.getTop() + bounds.getBottom() - 9) / 2 + 1;

        if (textWidth > bounds.getWidth()) {
            final var widthDifference = textWidth - bounds.getWidth();
            final var seconds = (double) Util.getMillis() / 1000.0;

            enableScissor(guiGraphics, bounds);
            drawText(
                    guiGraphics,
                    text,
                    bounds.getLeft() - (int)Mth.lerp(Math.sin(1.5707963267948966 * Math.cos(
                            6.283185307179586 * seconds / Math.max((double) widthDifference * 0.5, 3.0)
                    )) / 2.0 + 0.5, 0.0, widthDifference),
                    y,
                    color
            );
            disableScissor(guiGraphics);
        } else {
            drawCenteredText(guiGraphics, text, (bounds.getLeft() + bounds.getRight()) / 2, y, color);
        }
    }

    public static void drawCenteredMessage(
            @NotNull GuiGraphics guiGraphics,
            @NotNull AbstractWidget widget,
            int localX,
            int localY,
            int color
    ) {
        drawCenteredText(guiGraphics, widget.getMessage(), widget.getX() + localX, widget.getY() + localY, color);
    }

    public static void drawMessage(
            @NotNull GuiGraphics guiGraphics,
            @NotNull AbstractWidget widget,
            int localX,
            int localY,
            int color
    ) {
        drawMessage(guiGraphics, widget, localX, localY, color, true);
    }

    public static void drawMessage(
            @NotNull GuiGraphics guiGraphics,
            @NotNull AbstractWidget widget,
            int localX,
            int localY,
            int color,
            boolean shadow
    ) {
        drawText(guiGraphics, widget.getMessage(), widget.getX() + localX, widget.getY() + localY, color, shadow);
    }

    public static void drawCenteredText(
            @NotNull GuiGraphics guiGraphics,
            @NotNull Component text,
            int x,
            int y,
            int color
    ) {
        guiGraphics.drawCenteredString(
                Minecraft.getInstance().font,
                text,
                x,
                y,
                color
        );
    }

    public static void drawCenteredText(
            @NotNull GuiGraphics guiGraphics,
            @NotNull String text,
            int x,
            int y,
            int color
    ) {
        guiGraphics.drawCenteredString(
                Minecraft.getInstance().font,
                text,
                x,
                y,
                color
        );
    }

    public static void drawText(
            @NotNull GuiGraphics guiGraphics,
            @NotNull Component text,
            int x,
            int y,
            int color
    ) {
        drawText(guiGraphics, text, x, y, color, true);
    }

    public static void drawText(
            @NotNull GuiGraphics guiGraphics,
            @NotNull Component text,
            int x,
            int y,
            int color,
            boolean shadow
    ) {
        guiGraphics.drawString(Minecraft.getInstance().font, text, x, y, color, shadow);
    }

    public static void drawText(
            @NotNull GuiGraphics guiGraphics,
            @NotNull String text,
            int x,
            int y,
            int color
    ) {
        drawText(guiGraphics, text, x, y, color, true);
    }

    public static void drawText(
            @NotNull GuiGraphics guiGraphics,
            @NotNull String text,
            int x,
            int y,
            int color,
            boolean shadow
    ) {
        guiGraphics.drawString(Minecraft.getInstance().font, text, x, y, color, shadow);
    }

    public static void enableScissor(@NotNull GuiGraphics guiGraphics, @NotNull Rect2i bounds) {
        guiGraphics.enableScissor(bounds.getLeft(), bounds.getTop(), bounds.getRight(), bounds.getBottom());
    }

    public static void disableScissor(@NotNull GuiGraphics guiGraphics) {
        guiGraphics.disableScissor();
    }

    public static void playClickSound() {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }
}
