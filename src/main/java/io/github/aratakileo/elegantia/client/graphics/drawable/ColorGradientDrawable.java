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

    public @NotNull ColorGradientDrawable setColors(int colorStart, int colorEnd) {
        this.colorStart = colorStart;
        this.colorEnd = colorEnd;

        return this;
    }

    public @NotNull ColorGradientDrawable setColorStart(int colorStart) {
        this.colorStart = colorStart;
        return this;
    }

    public @NotNull ColorGradientDrawable setColorEnd(int colorEnd) {
        this.colorEnd = colorEnd;
        return this;
    }

    public @NotNull ColorGradientDrawable setGradientDirection(@NotNull RectDrawer.GradientDirection gradientDirection) {
        this.gradientDirection = gradientDirection;
        return this;
    }

    public @NotNull ColorGradientDrawable setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
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
