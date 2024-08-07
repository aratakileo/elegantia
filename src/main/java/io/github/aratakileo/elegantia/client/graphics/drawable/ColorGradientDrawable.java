package io.github.aratakileo.elegantia.client.graphics.drawable;

import io.github.aratakileo.elegantia.client.graphics.drawer.RectDrawer;
import org.jetbrains.annotations.NotNull;

public class ColorGradientDrawable implements Drawable {
    private int colorStart, colorEnd;
    private @NotNull RectDrawer.GradientDirection gradientDirection;
    protected int strokeColor = 0x0, strokeThickness = 0;

    public ColorGradientDrawable(
            int colorStart,
            int colorEnd,
            @NotNull RectDrawer.GradientDirection gradientDirection
    ) {
        this.colorStart = colorStart;
        this.colorEnd = colorEnd;
        this.gradientDirection = gradientDirection;
    }

    public @NotNull ColorGradientDrawable setRgbColors(int rgbColorStart, int rgbColorEnd) {
        this.colorStart = rgbColorStart | 0xff000000;
        this.colorEnd = rgbColorEnd | 0xff000000;

        return this;
    }

    public @NotNull ColorGradientDrawable setRgbColorStart(int rgbColorStart) {
        this.colorStart = rgbColorStart | 0xff000000;
        return this;
    }

    public @NotNull ColorGradientDrawable setRgbColorEnd(int rgbColorEnd) {
        this.colorEnd = rgbColorEnd | 0xff000000;
        return this;
    }

    public @NotNull ColorGradientDrawable setColors(int argbColorStart, int argbColorEnd) {
        this.colorStart = argbColorStart;
        this.colorEnd = argbColorEnd;

        return this;
    }

    public @NotNull ColorGradientDrawable setColorStart(int argbColorStart) {
        this.colorStart = argbColorStart;
        return this;
    }

    public @NotNull ColorGradientDrawable setColorEnd(int argbColorEnd) {
        this.colorEnd = argbColorEnd;
        return this;
    }

    public @NotNull ColorGradientDrawable setGradientDirection(@NotNull RectDrawer.GradientDirection gradientDirection) {
        this.gradientDirection = gradientDirection;
        return this;
    }

    public @NotNull ColorGradientDrawable setStrokeRgbColor(int strokeRgbColor) {
        this.strokeColor = strokeRgbColor | 0xff000000;
        return this;
    }

    public @NotNull ColorGradientDrawable setStrokeColor(int strokeArgbColor) {
        this.strokeColor = strokeArgbColor;
        return this;
    }

    public @NotNull ColorGradientDrawable setStrokeThickness(int strokeThickness) {
        this.strokeThickness = strokeThickness;
        return this;
    }

    @Override
    public void render(@NotNull RectDrawer rectDrawer) {
        rectDrawer.drawGradient(colorStart, colorEnd, gradientDirection);
        rectDrawer.drawStroke(strokeColor, strokeThickness);
    }
}
