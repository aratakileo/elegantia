package io.github.aratakileo.elegantia.gui;

import io.github.aratakileo.elegantia.util.Rect2i;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
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

    class HoveredTooltipPositioner implements TooltipPositioner {
        @Override
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
            vector2i.x = bounds.getX() + 3;
            vector2i.y = bounds.getY() + bounds.getHeight() + 3 + 1;
            if (vector2i.y + height + 3 > screenHeight) {
                vector2i.y = bounds.getY() - height - 3 - 1;
            }

            if (vector2i.x + width > screenWidth) {
                vector2i.x = Math.max(bounds.getX() + bounds.getWidth() - width - 3, 4);
            }

            return vector2i;
        }
    }
}
