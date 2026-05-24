package io.github.aratakileo.elegantia.common.data;

import io.github.aratakileo.elegantia.common.environment.Origin;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

public final class AttachmentRegistry {
    private static final HashMap<Identifier, AttachmentKey<?>> attachmentKeys = new HashMap<>();

    private AttachmentRegistry() {}

    public static <T, A extends AttachmentKey<T>> @NotNull A register(@NotNull A attachmentKey) {
        attachmentKeys.put(attachmentKey.id(), attachmentKey);
        return attachmentKey;
    }

    public static @NotNull Optional<AttachmentKey<?>> optional(@NotNull Identifier id) {
        return Optional.ofNullable(attachmentKeys.get(id));
    }

    public static @NotNull Optional<AttachmentKey<?>> optional(@NotNull Origin namespace, @NotNull String name) {
        return Optional.ofNullable(attachmentKeys.get(namespace.id(name)));
    }

    public static @NotNull AttachmentKey<?> unwrap(@NotNull Identifier id) {
        return Objects.requireNonNull(attachmentKeys.get(id));
    }

    public static @NotNull AttachmentKey<?> unwrap(@NotNull Origin namespace, @NotNull String name) {
        return Objects.requireNonNull(attachmentKeys.get(namespace.id(name)));
    }

    public static boolean has(@NotNull Identifier id) {
        return attachmentKeys.containsKey(id);
    }

    public static boolean has(@NotNull AttachmentKey<?> attachmentKey) {
        return attachmentKeys.containsKey(attachmentKey.id());
    }

    public static boolean has(@NotNull Origin namespace, @NotNull String name) {
        return attachmentKeys.containsKey(namespace.id(name));
    }
}
