package io.github.aratakileo.elegantia.client.graphics.drawable;

import io.github.aratakileo.elegantia.client.graphics.drawer.RectDrawer;
import org.jetbrains.annotations.NotNull;

public class ElegantiaButtonDrawable extends InteractionDrawable {
    @Override
    public void render(@NotNull RectDrawer rectDrawer) {
        if (pressed) rectDrawer.bounds.expandBounds(-1);

        rectDrawer.draw(0xaa222222).drawStroke(
                hoveredOrFocused(rectDrawer.bounds)
                        ? 0xffffffff
                        : 0xaa000000,
                1
        );
    }
}
