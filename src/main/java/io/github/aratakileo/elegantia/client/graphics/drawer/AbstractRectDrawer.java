package io.github.aratakileo.elegantia.client.graphics.drawer;

import io.github.aratakileo.elegantia.client.graphics.ElGuiGraphics;
import io.github.aratakileo.elegantia.core.math.Rect2i;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractRectDrawer<D extends AbstractRectDrawer<D>> {
    public final ElGuiGraphics guiGraphics;
    public final Rect2i bounds;

    public AbstractRectDrawer(@NotNull ElGuiGraphics guiGraphics, @NotNull Rect2i bounds) {
        this.guiGraphics = guiGraphics;
        this.bounds = bounds;
    }

    abstract public @NotNull D withNewBounds(@NotNull Rect2i bounds);
}
