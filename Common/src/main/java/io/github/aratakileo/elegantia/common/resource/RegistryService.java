package io.github.aratakileo.elegantia.common.resource;

import io.github.aratakileo.elegantia.common.environment.Origin;
import io.github.aratakileo.elegantia.core.util.Exceptions;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Supplier;

public interface RegistryService {
    <T> @NotNull RegistryContainer<T> register(
            @NotNull Registry<T> registry,
            @NotNull Identifier id,
            @NotNull Supplier<T> fabric
    );

    default <T> @NotNull RegistryContainer<T> register(
            @NotNull Registry<T> registry,
            @NotNull Origin namespace,
            @NotNull String name,
            @NotNull Supplier<T> fabric
    ) {
        return register(registry, namespace.id(name), fabric);
    }

    <T> @NotNull Optional<T> getValue(@NotNull Registry<T> registry, @NotNull Identifier id);

    default <T> @NotNull Optional<T> getValue(
            @NotNull Registry<T> registry,
            @NotNull Origin namespace,
            @NotNull String name
    ) {
        return getValue(registry, namespace.id(name));
    }

    final class Storage {
        private static volatile RegistryService INSTANCE = null;

        private Storage() {}
    }

    static @NotNull RegistryService instance() {
        if (RegistryService.Storage.INSTANCE == null)
            throw new IllegalStateException(RegistryService.class.getName() + " was accessed before initialization!");

        return RegistryService.Storage.INSTANCE;
    }

    static void setInstance(@NotNull RegistryService service) {
        if (Exceptions.throwOrLogIf(
                RegistryService.Storage.INSTANCE != null,
                Origin.ELEGANTIA,
                IllegalStateException::new,
                "{} instance is already set!",
                RegistryService.class.getName()
        )) return;

        RegistryService.Storage.INSTANCE = service;
    }
}
