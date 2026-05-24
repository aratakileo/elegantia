package io.github.aratakileo.elegantia.common.data.impl;

import com.mojang.serialization.Codec;
import io.github.aratakileo.elegantia.common.data.AttachmentKey;
import io.github.aratakileo.elegantia.common.environment.Origin;
import io.github.aratakileo.elegantia.common.data.AttachmentRegistry;
import io.github.aratakileo.elegantia.core.util.Strings;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Supplier;

public final class AttachmentImpl<T> implements AttachmentKey<T> {
    private final Identifier id;
    private final Codec<T> persistentCodec;
    private final Supplier<T> defaultValueGetter;

    private AttachmentImpl(
            @NotNull Identifier id,
            @NotNull Codec<T> persistentCodec,
            @Nullable Supplier<T> defaultValueGetter
    ) {
        this.id = id;
        this.persistentCodec = persistentCodec;
        this.defaultValueGetter = defaultValueGetter;
    }

    @Override
    public @NotNull Identifier id() {
        return id;
    }

    @Override
    public @NotNull Codec<T> persistentCodec() {
        return persistentCodec;
    }

    @Override
    public @Nullable T defaultValue() {
        return defaultValueGetter == null ? null : defaultValueGetter.get();
    }

    @Override
    public String toString() {
        return Strings.format(
                "{}[`{}` -> `{}` or default `{}`]",
                this.getClass().getSimpleName(),
                id,
                persistentCodec,
                defaultValue()
        );
    }

    public final static class Builder<T> {
        private final Identifier id;

        private Codec<T> persistentCodec = null;
        private Supplier<T> defaultValueGetter = null;

        private Builder(@NotNull Identifier id) {
            this.id = id;
        }

        @SuppressWarnings("unchecked")
        public <A> @NotNull Builder<A> defaultValue(@NotNull Supplier<A> getter) {
            if (persistentCodec != null) {
                final var builder = new Builder<A>(id);
                builder.persistentCodec = (Codec<A>) this.persistentCodec;
                builder.defaultValueGetter = getter;
                return builder;
            }

            defaultValueGetter = (Supplier<T>)getter;
            return (Builder<A>) this;
        }

        @SuppressWarnings("unchecked")
        public <A> @NotNull Builder<A> persistent(@NotNull Codec<A> codec) {
            if (defaultValueGetter != null) {
                final var builder = new Builder<A>(id);
                builder.defaultValueGetter = (Supplier<A>) this.defaultValueGetter;
                builder.persistentCodec = codec;
                return builder;
            }

            this.persistentCodec = (Codec<T>) codec;
            return (Builder<A>) this;
        }

        public @NotNull Builder<Byte> persistentByte() {
            return persistent(Codec.BYTE);
        }

        public @NotNull Builder<Short> persistentShort() {
            return persistent(Codec.SHORT);
        }

        public @NotNull Builder<Integer> persistentInt() {
            return persistent(Codec.INT);
        }

        public @NotNull Builder<Long> persistentLong() {
            return persistent(Codec.LONG);
        }

        public @NotNull Builder<Float> persistentFloat() {
            return persistent(Codec.FLOAT);
        }

        public @NotNull Builder<Double> persistentDouble() {
            return persistent(Codec.DOUBLE);
        }

        public @NotNull Builder<Boolean> persistentBool() {
            return persistent(Codec.BOOL);
        }

        public @NotNull Builder<String> persistentString() {
            return persistent(Codec.STRING);
        }

        public @NotNull Builder<Identifier> persistentId() {
            return persistent(Identifier.CODEC);
        }

        public @NotNull Builder<UUID> persistentUuid() {
            return persistent(UUIDUtil.CODEC);
        }

        public @NotNull AttachmentImpl<T> build() {
            return new AttachmentImpl<>(id, persistentCodec, defaultValueGetter);
        }

        public @NotNull AttachmentImpl<T> buildAndRegister() {
            return AttachmentRegistry.register(build());
        }
    }

    public static <T> @NotNull Builder<T> builder(@NotNull Identifier id) {
        return new Builder<>(id);
    }

    public static <T> @NotNull Builder<T> builder(@NotNull Origin namespace, @NotNull String key) {
        return new Builder<>(namespace.id(key));
    }
}
