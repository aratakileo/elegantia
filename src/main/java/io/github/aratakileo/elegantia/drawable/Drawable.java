package io.github.aratakileo.elegantia.drawable;

import io.github.aratakileo.elegantia.math.Rect2i;
import io.github.aratakileo.elegantia.util.graphics.RectDrawer;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;

public abstract class Drawable {
    protected int strokeColor = 0x0, strokeThickness = 0;

    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
    }

    public void setStrokeThickness(int strokeThickness) {
        this.strokeThickness = strokeThickness;
    }

    protected abstract void render(@NotNull RectDrawer rectDrawer, float dt);

    public void render(@NotNull GuiGraphics guiGraphics, @NotNull Rect2i bounds, float dt) {
        final var rectDrawer = new RectDrawer(guiGraphics, bounds);

        render(rectDrawer, dt);

        rectDrawer.drawStroke(strokeColor, strokeThickness);
    }
}
