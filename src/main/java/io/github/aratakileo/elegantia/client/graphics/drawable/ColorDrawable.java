package io.github.aratakileo.elegantia.client.graphics.drawable;

import io.github.aratakileo.elegantia.client.graphics.drawer.RectDrawer;
import org.jetbrains.annotations.NotNull;

public class ColorDrawable implements Drawable {
    private int color, strokeColor = 0x0, strokeThickness = 0;

    public ColorDrawable(int color) {
        this.color = color;
    }

    public @NotNull ColorDrawable setColor(int color) {
        this.color = color;
        return this;
    }

    public @NotNull ColorDrawable setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
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
