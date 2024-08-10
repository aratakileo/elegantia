package io.github.aratakileo.elegantia.core.math;

import org.jetbrains.annotations.NotNull;

public interface VectorInterface<V> {
    @NotNull V neg();
    @NotNull V abs();

    @NotNull V sub(@NotNull V vector);
    @NotNull V add(@NotNull V vector);
    @NotNull V mul(@NotNull V vector);
    @NotNull V div(@NotNull V vector);

    @NotNull V perpendicular();
    @NotNull V normalize();

    @NotNull V copy();
}
