package io.github.aratakileo.elegantia.graphics.drawable;

import io.github.aratakileo.elegantia.graphics.drawer.RectDrawer;
import org.jetbrains.annotations.NotNull;

public class SimpleInteractionDrawable implements InteractableDrawable {
    @Override
    public void renderInteraction(@NotNull RectDrawer rectDrawer, @NotNull InteractionState interactionState) {
        if (interactionState.pressed())
            rectDrawer.bounds.moveBounds(1, 1, -1, -1);

        rectDrawer.draw(0xaa222222).drawStroke(
                interactionState.hoveredOrFocused() ? 0xffffffff : 0xaa000000, 1
        );
    }
}
