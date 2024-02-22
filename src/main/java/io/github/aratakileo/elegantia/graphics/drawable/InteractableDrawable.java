package io.github.aratakileo.elegantia.graphics.drawable;

public abstract class InteractableDrawable extends Drawable {
    protected boolean isHoveredOrFocused, isPressed, isEnabled;

    public void setState(boolean isHoveredOrFocused, boolean isPressed, boolean isEnabled) {
        this.isHoveredOrFocused = isHoveredOrFocused;
        this.isPressed = isPressed;
        this.isEnabled = isEnabled;
    }
}
