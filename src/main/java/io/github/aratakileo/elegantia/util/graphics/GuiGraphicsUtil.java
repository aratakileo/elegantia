package io.github.aratakileo.elegantia.util.graphics;

import io.github.aratakileo.elegantia.gui.widget.AbstractWidget;
import io.github.aratakileo.elegantia.math.Rect2i;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class GuiGraphicsUtil {
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
