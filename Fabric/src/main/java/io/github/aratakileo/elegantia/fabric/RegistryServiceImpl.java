package io.github.aratakileo.elegantia.fabric;

import io.github.aratakileo.elegantia.common.resource.RegistryContainer;
import io.github.aratakileo.elegantia.common.resource.RegistryService;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Supplier;

public final class RegistryServiceImpl implements RegistryService {
    @Override
    public <T> @NotNull RegistryContainer<T> register(@NotNull Registry<T> registry, @NotNull Identifier id, @NotNull Supplier<T> fabric) {
        return RegistryContainer.create(Registry.register(registry, id, fabric.get()));
    }

    @Override
    public <T> @NotNull Optional<T> getValue(@NotNull Registry<T> registry, @NotNull Identifier id) {
        return Optional.ofNullable(registry.getValue(id));
    }

    private RegistryServiceImpl() {}

    public static void impl() {
        RegistryService.setInstance(new RegistryServiceImpl());
    }
}
