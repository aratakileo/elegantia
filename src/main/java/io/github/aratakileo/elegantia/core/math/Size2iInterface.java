package io.github.aratakileo.elegantia.core.math;

import org.jetbrains.annotations.NotNull;

public interface Size2iInterface {
    int width();
    int height();

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

    @NotNull Size2iInterface copy();
}
