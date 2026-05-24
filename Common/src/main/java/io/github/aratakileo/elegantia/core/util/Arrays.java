package io.github.aratakileo.elegantia.core.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Arrays {
    private Arrays() {}

    public static <T> @NotNull T @NotNull[] expandWith(final @NotNull T @NotNull[] arr, @NotNull T newValue) {
        final var newArray = java.util.Arrays.copyOf(arr, arr.length + 1);
        newArray[newArray.length - 1] = newValue;
        return newArray;
    }

    public static <T> @Nullable T @NotNull[] expandWithNullable(final @Nullable T @NotNull[] arr, @Nullable T newValue) {
        final var newArray = java.util.Arrays.copyOf(arr, arr.length + 1);
        newArray[newArray.length - 1] = newValue;
        return newArray;
    }
}
