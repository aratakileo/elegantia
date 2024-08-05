package io.github.aratakileo.elegantia.graphics.drawer;

import io.github.aratakileo.elegantia.math.Rect2i;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractDrawer<D extends AbstractDrawer<D>> {
    public final GuiGraphics guiGraphics;
    public final Rect2i bounds;

    public AbstractDrawer(@NotNull GuiGraphics guiGraphics, @NotNull Rect2i bounds) {
        this.guiGraphics = guiGraphics;
        this.bounds = bounds;
    }

    abstract public @NotNull D withNewBounds(@NotNull Rect2i bounds);
}
