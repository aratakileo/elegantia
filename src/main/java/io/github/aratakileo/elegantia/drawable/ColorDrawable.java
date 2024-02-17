package io.github.aratakileo.elegantia.drawable;

import io.github.aratakileo.elegantia.util.graphics.RectDrawer;
import org.jetbrains.annotations.NotNull;

public class ColorDrawable extends Drawable {
    private int color;

    public ColorDrawable(int color) {
        this.color = color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    protected void render(@NotNull RectDrawer rectDrawer, float dt) {
        rectDrawer.draw(color);
    }
}
