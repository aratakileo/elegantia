package io.github.aratakileo.elegantia.drawable;

import io.github.aratakileo.elegantia.util.graphics.RectDrawer;
import org.jetbrains.annotations.NotNull;

public class ColorGradientDrawable extends Drawable {
    private int colorStart, colorEnd;

    public ColorGradientDrawable(int colorStart, int colorEnd) {
        this.colorStart = colorStart;
        this.colorEnd = colorEnd;
    }

    public void setColorStart(int colorStart) {
        this.colorStart = colorStart;
    }

    public void setColorEnd(int colorEnd) {
        this.colorEnd = colorEnd;
    }

    @Override
    protected void render(@NotNull RectDrawer rectDrawer, float dt) {
        rectDrawer.drawGradient(colorStart, colorEnd);
    }
}
