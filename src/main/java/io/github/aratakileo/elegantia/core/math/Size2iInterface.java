package io.github.aratakileo.elegantia.core.math;

import org.jetbrains.annotations.NotNull;

public interface Size2iInterface {
    int width();
    int height();

    default double get(int index) {
        return switch (index) {
            case 0 -> width();
            case 1 -> height();
            default -> throw new IllegalArgumentException();
        };
    }

    @NotNull Size2iInterface expand(int size);
    @NotNull Size2iInterface expand(@NotNull Size2iInterface size);
    @NotNull Size2iInterface expand(@NotNull Vector2iInterface vec2i);
    @NotNull Size2iInterface expand(int width, int height);

    @NotNull Size2iInterface shrink(int size);
    @NotNull Size2iInterface shrink(@NotNull Size2iInterface size);
    @NotNull Size2iInterface shrink(@NotNull Vector2iInterface vec2i);
    @NotNull Size2iInterface shrink(int width, int height);

    @NotNull Size2iInterface scale(int value);
    @NotNull Size2iInterface scale(float value);
    @NotNull Size2iInterface scale(double value);

    default int max() {
        return Math.max(width(), height());
    }

    default int min() {
        return Math.max(width(), height());
    }

    @NotNull Size2iInterface max(@NotNull Size2iInterface size2iInterface);
    @NotNull Size2iInterface min(@NotNull Size2iInterface size2iInterface);

    @NotNull Size2iInterface copy();

    @NotNull Vector2iInterface asVec2i();
    @NotNull Vector2dInterface asVec2d();
    @NotNull Vector2fInterface asVec2f();
}
