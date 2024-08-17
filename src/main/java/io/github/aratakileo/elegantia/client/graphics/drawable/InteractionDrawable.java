package io.github.aratakileo.elegantia.client.graphics.drawable;

import io.github.aratakileo.elegantia.client.MouseProvider;
import io.github.aratakileo.elegantia.client.graphics.drawer.RectDrawer;
import io.github.aratakileo.elegantia.core.BuiltinTextures;
import io.github.aratakileo.elegantia.core.math.Rect2i;
import org.jetbrains.annotations.NotNull;

public abstract class InteractionDrawable implements Drawable {
    protected boolean focused = false, pressed = false, enabled = false;

    public @NotNull InteractionDrawable setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public @NotNull InteractionDrawable setFocused(boolean focused) {
        this.focused = focused;
        return this;
    }

    public @NotNull InteractionDrawable setPressed(boolean pressed) {
        this.pressed = pressed;
        return this;
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

    public static @NotNull InteractionDrawable createMinecraftButton() {
        return new InteractionDrawable() {
            @Override
            public void render(@NotNull RectDrawer rectDrawer) {
                if (hoveredOrFocused(rectDrawer.bounds)) {
                    BuiltinTextures.ELASTIC_MINECRAFT_BUTTON_FOCUSED.get().render(rectDrawer);
                    return;
                }

                if (!enabled) {
                    BuiltinTextures.ELASTIC_MINECRAFT_BUTTON_DISABLED.get().render(rectDrawer);
                    return;
                }

                BuiltinTextures.ELASTIC_MINECRAFT_BUTTON.get().render(rectDrawer);
            }
        };
    }
}
