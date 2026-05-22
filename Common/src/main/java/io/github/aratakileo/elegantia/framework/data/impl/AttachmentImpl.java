package io.github.aratakileo.elegantia.framework.data.impl;

import com.mojang.serialization.Codec;
import io.github.aratakileo.elegantia.framework.data.AttachmentKey;
import io.github.aratakileo.elegantia.core.environment.Origin;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        return String.format(
                "%s{ `%s` -> `%s` or default `%s` }",
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

        public @NotNull Builder<T> defaultValue(@NotNull Supplier<T> getter) {
            defaultValueGetter = getter;
            return this;
        }

        public @NotNull Builder<T> persistent(@NotNull Codec<T> codec) {
            persistentCodec = codec;
            return this;
        }

        public @NotNull AttachmentImpl<T> build() {
            return new AttachmentImpl<>(id, persistentCodec, defaultValueGetter);
        }
    }

    public static <T> @NotNull Builder<T> builder(@NotNull Identifier id) {
        return new Builder<>(id);
    }

    public static <T> @NotNull Builder<T> builder(@NotNull Origin namespace, @NotNull String key) {
        return new Builder<>(namespace.id(key));
    }
}
