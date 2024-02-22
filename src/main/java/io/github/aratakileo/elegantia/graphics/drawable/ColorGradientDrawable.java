package io.github.aratakileo.elegantia.graphics.drawable;

import io.github.aratakileo.elegantia.graphics.RectDrawer;
import org.jetbrains.annotations.NotNull;

public class ColorGradientDrawable extends Drawable {
    private int colorStart, colorEnd;
    private @NotNull RectDrawer.GradientDirection gradientDirection;

    public ColorGradientDrawable(
            int colorStart,
            int colorEnd,
            @NotNull RectDrawer.GradientDirection gradientDirection
    ) {
        this.colorStart = colorStart;
        this.colorEnd = colorEnd;
        this.gradientDirection = gradientDirection;
    }

    public void setColorStart(int colorStart) {
        this.colorStart = colorStart;
    }

    public void setColorEnd(int colorEnd) {
        this.colorEnd = colorEnd;
    }

    public void setGradientDirection(@NotNull RectDrawer.GradientDirection gradientDirection) {
        this.gradientDirection = gradientDirection;
    }

    @Override
    protected void render(@NotNull RectDrawer rectDrawer, float dt) {
        rectDrawer.drawGradient(colorStart, colorEnd, gradientDirection);
    }
}
