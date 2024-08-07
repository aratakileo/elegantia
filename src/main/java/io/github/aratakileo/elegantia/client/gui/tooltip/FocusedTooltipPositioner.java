package io.github.aratakileo.elegantia.client.gui.tooltip;


import io.github.aratakileo.elegantia.core.math.Rect2i;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import org.joml.Vector2ic;

class FocusedTooltipPositioner implements TooltipPositioner {
    private final Rect2i bounds;

    public FocusedTooltipPositioner(Rect2i bounds) {
        this.bounds = bounds;
    }

    public @NotNull Vector2ic positionTooltip(
            int screenWidth,
            int screenHeight,
            int x,
            int y,
            int width,
            int height
    ) {
        final var vector2i = new Vector2i();
        vector2i.x = bounds.x + 3;
        vector2i.y = bounds.y + bounds.height + 3 + 1;
        if (vector2i.y + height + 3 > screenHeight) {
            vector2i.y = bounds.y - height - 3 - 1;
        }

        if (vector2i.x + width > screenWidth) {
            vector2i.x = Math.max(bounds.x + bounds.width - width - 3, 4);
        }

        return vector2i;
    }
}
