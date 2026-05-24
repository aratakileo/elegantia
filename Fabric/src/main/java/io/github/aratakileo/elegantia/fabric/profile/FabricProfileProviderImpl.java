package io.github.aratakileo.elegantia.fabric.profile;

import io.github.aratakileo.elegantia.common.environment.ModProfile;
import io.github.aratakileo.elegantia.common.environment.ProfileProvider;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class FabricProfileProviderImpl implements ProfileProvider {
    public static final FabricProfileProviderImpl INSTANCE = new FabricProfileProviderImpl();

    private FabricProfileProviderImpl() {}

    @Override
    public @NotNull Optional<ModProfile> modOptional(@NotNull String id) {
        return FabricLoader.getInstance()
                .getModContainer(id)
                .map(FabricModProfileImpl::from);
    }
}
