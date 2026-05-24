package io.github.aratakileo.elegantia.common.data;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface AttachmentGroup {
    <T> @NotNull Optional<T> optional(@NotNull AttachmentKey<T> key);
    <T> void set(@NotNull AttachmentKey<T> key, @Nullable T value);
    <T> boolean has(@NotNull AttachmentKey<T> key);
    void write(@NotNull ValueOutput output);
    void read(@NotNull ValueInput input);

    default <T> @NotNull T unwrap(@NotNull AttachmentKey<T> key) {
        return optional(key).orElseThrow();
    }
}
