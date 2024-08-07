package io.github.aratakileo.elegantia.client.graphics.drawable;

import io.github.aratakileo.elegantia.client.graphics.drawer.RectDrawer;
import org.jetbrains.annotations.NotNull;

public interface InteractableDrawable extends Drawable {
    void renderInteraction(@NotNull RectDrawer rectDrawer, @NotNull InteractionState interactionState);

    @Override
    default void render(@NotNull RectDrawer rectDrawer) {
        renderInteraction(rectDrawer, InteractionState.NO_INTERACTION);
    }

    record InteractionState(boolean hovered, boolean focused, boolean pressed, boolean enabled) {
        public static final InteractionState NO_INTERACTION = new InteractionState(
                false,
                false,
                false,
                false
        );

        public boolean hoveredOrFocused() {
            return hovered || focused;
        }
    }
}
