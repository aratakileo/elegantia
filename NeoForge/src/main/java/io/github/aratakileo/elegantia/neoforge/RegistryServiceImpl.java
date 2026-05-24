package io.github.aratakileo.elegantia.neoforge;

import io.github.aratakileo.elegantia.common.environment.Origin;
import io.github.aratakileo.elegantia.common.resource.RegistryContainer;
import io.github.aratakileo.elegantia.common.resource.RegistryService;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class RegistryServiceImpl implements RegistryService {
    private final static HashMap<Registry<?>, HashMap<Origin, DeferredRegister<?>>> REGISTRIES;
    private static ModContainer contextInstance = null;

    @Override
    public <T> @NotNull RegistryContainer<T> register(
            @NotNull Registry<T> registry,
            @NotNull Identifier id,
            @NotNull Supplier<T> fabric
    ) {
        registry(registry, Origin.from(id)).register(id.getPath(), fabric);
        return RegistryContainer.create(registry, id);
    }

    @Override
    public @NotNull <T> Optional<T> getValue(@NotNull Registry<T> registry, @NotNull Identifier id) {
        return Optional.ofNullable(registry.getValue(id));
    }

    private RegistryServiceImpl() {}

    public static void impl(@NotNull ModContainer context) {
        RegistryService.setInstance(new RegistryServiceImpl());
        contextInstance = context;
    }

    @SuppressWarnings("unchecked")
    public static <T> @NotNull DeferredRegister<T> registry(@NotNull Registry<T> registry, @NotNull Origin namespace) {
        if (!REGISTRIES.containsKey(registry))
            REGISTRIES.put(registry, new HashMap<>());

        final var registries = REGISTRIES.get(registry);

        if (!registries.containsKey(namespace)) {
            final var newRegister = DeferredRegister.create(registry, namespace.key);

            newRegister.register(Objects.requireNonNull(contextInstance.getEventBus()));

            registries.put(namespace, newRegister);
        }

        return (DeferredRegister<T>) registries.get(namespace);
    }

    static {
        REGISTRIES = new HashMap<>();
    }
}