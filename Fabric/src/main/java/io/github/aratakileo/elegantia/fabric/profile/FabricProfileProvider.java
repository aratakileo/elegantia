package io.github.aratakileo.elegantia.fabric.profile;

import io.github.aratakileo.elegantia.core.environment.ModProfile;
import io.github.aratakileo.elegantia.core.environment.ProfileProvider;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class FabricProfileProvider implements ProfileProvider {
    public static final FabricProfileProvider INSTANCE = new FabricProfileProvider();

    private FabricProfileProvider() {}

    @Override
    public @NotNull Optional<ModProfile> modOptional(@NotNull String id) {
        return FabricLoader.getInstance()
                .getModContainer(id)
                .map(FabricModProfile::from);
    }
}
