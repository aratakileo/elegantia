package io.github.aratakileo.elegantia.client.graphics.drawable;

import io.github.aratakileo.elegantia.client.graphics.drawer.RectDrawer;
import org.jetbrains.annotations.NotNull;

public class ColorDrawable implements Drawable {
    private int color, strokeColor = 0x0, strokeThickness = 0;

    public ColorDrawable(int color) {
        this.color = color;
    }

    public @NotNull ColorDrawable setRgbColor(int rgbColor) {
        this.color = rgbColor | 0xff000000;
        return this;
    }

    public @NotNull ColorDrawable setColor(int argbColor) {
        this.color = argbColor;
        return this;
    }

    public @NotNull ColorDrawable setStrokeRgbColor(int strokeRgbColor) {
        this.strokeColor = strokeRgbColor | 0xff000000;
        return this;
    }

    public @NotNull ColorDrawable setStrokeColor(int strokeArgbColor) {
        this.strokeColor = strokeArgbColor;
        return this;
    }

    public @NotNull ColorDrawable setStrokeThickness(int strokeThickness) {
        this.strokeThickness = strokeThickness;
        return this;
    }

    @Override
    public void render(@NotNull RectDrawer rectDrawer) {
        rectDrawer.draw(color);
        rectDrawer.drawStroke(strokeColor, strokeThickness);
    }
}
