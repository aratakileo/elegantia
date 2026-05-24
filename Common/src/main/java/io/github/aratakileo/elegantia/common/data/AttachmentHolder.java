package io.github.aratakileo.elegantia.common.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface AttachmentHolder<A extends AttachmentHolder<A>> {
    <T> @NotNull Optional<T> optional(@NotNull AttachmentKey<T> key);
    <T> @NotNull T unwrap(@NotNull AttachmentKey<T> key);

    <T> AttachmentHolder<A> set(@NotNull AttachmentKey<T> key, @Nullable T value);
    <T> boolean has(@NotNull AttachmentKey<T> key);
}
