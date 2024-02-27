package io.github.aratakileo.elegantia.gui.tooltip;

import io.github.aratakileo.elegantia.math.Rect2i;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import org.joml.Vector2ic;

class WidgetTooltipPositioner implements TooltipPositioner {
    private static final int MARGIN = 5;
    private static final int MOUSE_OFFSET_X = 12;
    public static final int MAX_OVERLAP_WITH_WIDGET = 3;
    public static final int MAX_DISTANCE_TO_WIDGET = 5;

    private final Rect2i bounds;

    public WidgetTooltipPositioner(@NotNull Rect2i bounds) {
        this.bounds = bounds;
    }

    @Override
    public @NotNull Vector2ic positionTooltip(
            int screenWidth,
            int screenHeight,
            int x,
            int y,
            int width,
            int height
    ) {
        final var vector2i = new Vector2i(x + MOUSE_OFFSET_X, y);
        if (vector2i.x + width > screenWidth - MARGIN) {
            vector2i.x = Math.max(x - MOUSE_OFFSET_X - width, 9);
        }

        vector2i.y += MAX_OVERLAP_WITH_WIDGET;

        int o = height + MAX_OVERLAP_WITH_WIDGET * 2;
        int p = bounds.getY()
                + bounds.getHeight()
                + MAX_OVERLAP_WITH_WIDGET
                + getOffset(0, 0, bounds.getHeight());

        int q = screenHeight - MAX_DISTANCE_TO_WIDGET;

        if (p + o <= q) vector2i.y += getOffset(vector2i.y, bounds.getY(), bounds.getHeight());
        else
            vector2i.y -= o + getOffset(vector2i.y, bounds.getY() + bounds.getHeight(), bounds.getHeight());

        return vector2i;
    }

    private static int getOffset(int tooltipY, int widgetY, int widgetHeight) {
        return Math.round(Mth.lerp(
                (float)Math.min(Math.abs(tooltipY - widgetY), widgetHeight) / (float)widgetHeight,
                (float)(widgetHeight - 3),
                5.0F
        ));
    }
}
