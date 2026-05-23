package io.github.aratakileo.elegantia.neoforge.profile;

import io.github.aratakileo.elegantia.core.environment.ModProfile;
import io.github.aratakileo.elegantia.core.environment.ProfileProvider;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class NeoForgeProfileProvider implements ProfileProvider {
    public static final NeoForgeProfileProvider INSTANCE = new NeoForgeProfileProvider();

    private NeoForgeProfileProvider() {}

    @Override
    public @NotNull Optional<ModProfile> modOptional(@NotNull String id) {
        return ModList.get().getModContainerById(id).map(NeoForgeModProfile::from);
    }
}
