package io.github.aratakileo.elegantia.gui.tooltip;

import io.github.aratakileo.elegantia.util.math.Rect2i;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2ic;

public interface TooltipPositioner extends ClientTooltipPositioner {
    @NotNull Vector2ic positionTooltip(
            int screenWidth,
            int screenHeight,
            int x,
            int y,
            int width,
            int height
    );

    static @NotNull TooltipPositioner getSimpleTooltipPositioner(@NotNull Rect2i bounds, boolean isFocused) {
        return isFocused ? new FocusedTooltipPositioner(bounds) : new WidgetTooltipPositioner(bounds);
    }
}
