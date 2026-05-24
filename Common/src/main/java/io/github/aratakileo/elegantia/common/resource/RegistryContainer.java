package io.github.aratakileo.elegantia.common.resource;

import io.github.aratakileo.elegantia.core.Result;
import io.github.aratakileo.elegantia.common.environment.Origin;
import io.github.aratakileo.elegantia.core.ThreadSafeInitializer;
import io.github.aratakileo.elegantia.core.util.Strings;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public final class RegistryContainer<T> {
    private Identifier id;
    private Registry<T> registry;

    private final String errMessage;
    private final ThreadSafeInitializer<T> initializer;

    private RegistryContainer(@Nullable Registry<T> registry, @Nullable Identifier id, @Nullable T value) {
        this.registry = registry;
        this.id = id;

        this.initializer = ThreadSafeInitializer.from(() -> {
            if (value != null) return value;

            final var fetched = RegistryService.instance().getValue(this.registry, this.id).orElse(null);

            if (fetched != null) {
                this.registry = null;
                this.id = null;
            }

            return fetched;
        });

        this.errMessage = (id != null && registry != null)
                ? Strings.format("`{}` in registry {}", id, registry)
                : "value itself";
    }

    public @NotNull T unwrap() {
        return initializer.unwrap(errMessage);
    }

    public @NotNull Optional<T> optional() {
        return initializer.optional();
    }

    public @NotNull Result<T> result() {
        return Result.fromOptional(
                optional(),
                RegistryValueNotFoundException::new,
                errMessage
        );
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
