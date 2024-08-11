package io.github.aratakileo.elegantia.core.math;

import io.github.aratakileo.elegantia.client.graphics.drawer.RectDrawer;
import org.jetbrains.annotations.NotNull;

public enum Corner {
    LEFT_TOP(180, 270),
    LEFT_BOTTOM(90, 180),
    RIGHT_BOTTOM(0, 90),
    RIGHT_TOP(270, 360);

    public final int startAngle, stopAngle;

    Corner(int startAngle, int stopAngle) {
        this.startAngle = startAngle;
        this.stopAngle = stopAngle;
    }

    public int spanAngle() {
        return stopAngle - startAngle;
    }

    public @NotNull Vector2dc getCenter(@NotNull Rect2i bounds, @NotNull RectDrawer.CornersRadius cornersRadius) {
        final var boundsLeftTop = bounds.getLeftTop().asVec2d();
        final var radius = cornersRadius.getFittedInRadius(this, bounds.getSize());

        return switch (this) {
            case LEFT_TOP -> boundsLeftTop.add(radius);
            case LEFT_BOTTOM -> boundsLeftTop.add(radius, bounds.height - radius);
            case RIGHT_BOTTOM -> bounds.getRightBottom().asVec2d().sub(radius);
            case RIGHT_TOP -> boundsLeftTop.add(bounds.width - radius, radius);
        };
    }
}
