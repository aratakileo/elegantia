package io.github.aratakileo.elegantia.graphics.drawable;

import io.github.aratakileo.elegantia.graphics.RectDrawer;
import org.jetbrains.annotations.NotNull;

public class ButtonDrawable extends InteractableDrawable {
    @Override
    protected void render(@NotNull RectDrawer rectDrawer, float dt) {
        if (isPressed)
            rectDrawer.bounds.moveIpBounds(1, 1, -1, -1);

        rectDrawer.draw(0xaa222222).drawStroke(isHoveredOrFocused ? 0xffffffff : 0xaa000000, 1);
    }
}
