package io.github.aratakileo.elegantia.graphics.drawable;

import io.github.aratakileo.elegantia.graphics.RectDrawer;
import org.jetbrains.annotations.NotNull;

public interface Drawable {
    void render(@NotNull RectDrawer rectDrawer, float dt);
}
