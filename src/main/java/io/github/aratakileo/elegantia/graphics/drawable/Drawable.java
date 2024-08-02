package io.github.aratakileo.elegantia.graphics.drawable;

import io.github.aratakileo.elegantia.graphics.drawer.RectDrawer;
import org.jetbrains.annotations.NotNull;

public interface Drawable {
    void render(@NotNull RectDrawer rectDrawer);
}
