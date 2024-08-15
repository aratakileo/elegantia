package io.github.aratakileo.elegantia.client.graphics.drawable;

import io.github.aratakileo.elegantia.client.MouseHandler;
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
        return bounds.contains(MouseHandler.getPosition()) || focused;
    }

    public boolean hovered(@NotNull Rect2i bounds) {
        return bounds.contains(MouseHandler.getPosition());
    }
}
