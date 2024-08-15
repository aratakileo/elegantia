package io.github.aratakileo.elegantia.client.graphics.drawable;

import io.github.aratakileo.elegantia.client.MouseProvider;
import io.github.aratakileo.elegantia.client.graphics.drawer.RectDrawer;
import io.github.aratakileo.elegantia.core.math.Rect2i;
import org.jetbrains.annotations.NotNull;

public abstract class InteractionDrawable implements Drawable {
    protected boolean focused = false, pressed = false, enabled = false;

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    public void setPressed(boolean pressed) {
        this.pressed = pressed;
    }

    public boolean hoveredOrFocused(@NotNull Rect2i bounds) {
        return bounds.contains(MouseProvider.getPosition()) || focused;
    }

    public boolean hovered(@NotNull Rect2i bounds) {
        return bounds.contains(MouseProvider.getPosition());
    }

    public static @NotNull InteractionDrawable createElegantiaButton() {
        return new InteractionDrawable() {
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
        };
    }
}
