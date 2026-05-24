package io.github.aratakileo.elegantia.common.data.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import io.github.aratakileo.elegantia.common.data.AttachmentGroup;
import io.github.aratakileo.elegantia.common.data.AttachmentKey;
import io.github.aratakileo.elegantia.common.data.AttachmentRegistry;
import io.github.aratakileo.elegantia.common.environment.Origin;
import io.github.aratakileo.elegantia.core.util.Classes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public final class AttachmentGroupImpl implements AttachmentGroup {
    public static final Identifier ELEGANTED_ID;

    private final ConcurrentHashMap<AttachmentKey<?>, Object> attachments = new ConcurrentHashMap<>();
    private final Identifier id;
    private final Codec<AttachmentGroupImpl> codec;

    public AttachmentGroupImpl(@NotNull Identifier id) {
        this.id = id;
        this.codec = codec(id);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> @NotNull Optional<T> optional(@NotNull AttachmentKey<T> key) {
        if (attachments.containsKey(key))
            return Optional.of((T) attachments.get(key));

        final var value = key.defaultValue();

        if (value == null) return Optional.empty();

        attachments.put(key, value);
        return Optional.of(value);
    }

    @Override
    public <T> void set(@NotNull AttachmentKey<T> key, @Nullable T value) {
        if (value == null) {
            attachments.remove(key);
            return;
        }

        attachments.put(key, value);
    }

    public void each(@NotNull BiConsumer<AttachmentKey<?>, Object> operator) {
        for (final var entry: attachments.entrySet())
            operator.accept(entry.getKey(), entry.getValue());
    }

    private <T> void decodeAndSet(@NotNull AttachmentKey<T> key, @NotNull Dynamic<?> src) {
        set(key, Objects.requireNonNull(key.persistentCodec()).parse(src).resultOrPartial().get());
    }

    @Override
    public <T> boolean has(@NotNull AttachmentKey<T> key) {
        return attachments.containsKey(key);
    }

    @Override
    @ApiStatus.Internal
    public void write(@NotNull ValueOutput output) {
        output.store(id.toString(), codec, this);
    }

    @Override
    @ApiStatus.Internal
    public void read(@NotNull ValueInput input) {
        input.read(id.toString(), codec).ifPresent(group -> attachments.putAll(group.attachments));
    }

    public static @NotNull Codec<AttachmentGroupImpl> codec(@NotNull Identifier id) {
        return Codec.unboundedMap(Identifier.CODEC, Codec.PASSTHROUGH).xmap(
                map -> {
                    final var result = new AttachmentGroupImpl(id);

                    for (final var entry: map.entrySet())
                        AttachmentRegistry.optional(entry.getKey())
                                .map(key -> key.persistentCodec() == null ? null : key)
                                .ifPresent(key -> result.decodeAndSet(key, entry.getValue()));

                    return result;
                },
                group -> {
                    final var result = new HashMap<Identifier, Dynamic<?>>();

                    group.each((key, value) -> {
                        if (key.persistentCodec() == null) return;
                        final var dynamicValue = key.encodeDynamicPersistent(Classes.quietCast(value)).unwrap();
                        result.put(key.id(), dynamicValue);
                    });

                    return result;
                }
        );
    }

    public static @NotNull AttachmentGroupImpl eleganted() {
        return new AttachmentGroupImpl(ELEGANTED_ID);
    }

    static {
        ELEGANTED_ID = Origin.ELEGANTIA.id("attachments");
    }
}
