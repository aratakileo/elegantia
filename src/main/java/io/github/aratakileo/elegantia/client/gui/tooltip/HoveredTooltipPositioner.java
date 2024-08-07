package io.github.aratakileo.elegantia.client.gui.tooltip;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import org.joml.Vector2ic;

public class HoveredTooltipPositioner implements TooltipPositioner {@Override
    public @NotNull Vector2ic positionTooltip(
            int screenWidth,
            int screenHeight,
            int x,
            int y,
            int width,
            int height
    ) {
        return new Vector2i(x - width - 7, y - height - 7).max(new Vector2i(7, 7));
    }
}
