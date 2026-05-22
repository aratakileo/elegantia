package io.github.aratakileo.elegantia.framework.resource.registry;

import io.github.aratakileo.elegantia.core.Result;
import io.github.aratakileo.elegantia.core.environment.Origin;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public final class RegistryContainer<T> {
    private Identifier id;
    private Registry<T> registry;
    private T value;

    private RegistryContainer(@Nullable Registry<T> registry, @Nullable Identifier id, @Nullable T value) {
        this.registry = registry;
        this.id = id;
        this.value = value;
    }

    public @NotNull T unwrap() {
        return Objects.requireNonNull(rawGet());
    }

    public @NotNull Optional<T> optional() {
        return Optional.ofNullable(rawGet());
    }

    public @NotNull Result<T> result() {
        return Result.fromNullable(
                rawGet(),
                RegistryValueNotFoundException::new,
                "`{}` in registry {}",
                id,
                registry
        );
    }

    private @Nullable T rawGet() {
        if (value != null) return value;

        value = RegistryService.instance().getValue(registry, id).orElse(null);

        if (value != null) {
            id = null;
            registry = null;
        }

        return value;
    }

    public static <T> @NotNull RegistryContainer<T> create(@NotNull T value) {
        return new RegistryContainer<>(null, null, value);
    }

    public static <T> @NotNull RegistryContainer<T> create(@NotNull Registry<T> registry, @NotNull Identifier id) {
        return new RegistryContainer<>(registry, id, null);
    }

    public static <T> @NotNull RegistryContainer<T> create(
            @NotNull Registry<T> registry,
            @NotNull Origin namespace,
            @NotNull String name
    ) {
        return new RegistryContainer<>(registry, namespace.id(name), null);
    }

    public static class RegistryValueNotFoundException extends RuntimeException {
        public RegistryValueNotFoundException(@NotNull String message) {
            super(message);
        }
    }
}
