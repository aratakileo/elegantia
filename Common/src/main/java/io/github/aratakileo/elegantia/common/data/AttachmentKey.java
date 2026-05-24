package io.github.aratakileo.elegantia.common.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import io.github.aratakileo.elegantia.core.Result;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public interface AttachmentKey<T> {
    @NotNull Identifier id();
    @Nullable Codec<T> persistentCodec();
    @Nullable T defaultValue();

    default @NotNull Result<Tag> encodePersistent(@NotNull T value) {
        final var optionalValue = Objects.requireNonNull(persistentCodec())
                .encodeStart(NbtOps.INSTANCE, value)
                .resultOrPartial();

        return Result.fromOptional(
                optionalValue,
                AttachmentEncodeException::new,
                id().toString()
        );
    }

    default @NotNull Result<Dynamic<Tag>> encodeDynamicPersistent(@NotNull T value) {
        final var optionalValue = Objects.requireNonNull(persistentCodec())
                .encodeStart(NbtOps.INSTANCE, value)
                .resultOrPartial()
                .map(tag -> new Dynamic<>(NbtOps.INSTANCE, tag));

        return Result.fromOptional(
                optionalValue,
                AttachmentEncodeException::new,
                id().toString()
        );
    }

    class AttachmentEncodeException extends RuntimeException {
        public AttachmentEncodeException(@NotNull String message) {
            super(message);
        }
    }
}
