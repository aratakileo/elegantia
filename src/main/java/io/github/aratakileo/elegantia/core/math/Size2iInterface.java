package io.github.aratakileo.elegantia.core.math;

import org.jetbrains.annotations.NotNull;

public interface Size2iInterface {
    int width();
    int height();

    @NotNull Size2iInterface add(int size);
    @NotNull Size2iInterface add(@NotNull Size2iInterface size);
    @NotNull Size2iInterface add(int width, int height);

    @NotNull Size2iInterface sub(int size);
    @NotNull Size2iInterface sub(@NotNull Size2iInterface size);
    @NotNull Size2iInterface sub(int width, int height);

    @NotNull Size2iInterface scale(int value);
    @NotNull Size2iInterface scale(float value);
    @NotNull Size2iInterface scale(double value);

    @NotNull Size2iInterface copy();
}
