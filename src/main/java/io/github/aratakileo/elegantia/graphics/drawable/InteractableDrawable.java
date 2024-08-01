package io.github.aratakileo.elegantia.graphics.drawable;

import io.github.aratakileo.elegantia.graphics.RectDrawer;
import org.jetbrains.annotations.NotNull;

public interface InteractableDrawable extends Drawable {
    void renderInteraction(
            @NotNull RectDrawer rectDrawer,
            @NotNull InteractionState interactionState,
            float dt
    );

    @Override
    default void render(@NotNull RectDrawer rectDrawer, float dt) {
        renderInteraction(rectDrawer, InteractionState.NO_INTERACTION, dt);
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
