package io.github.aratakileo.elegantia.neoforge.profile;

import io.github.aratakileo.elegantia.common.environment.ModProfile;
import io.github.aratakileo.elegantia.common.environment.ProfileProvider;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;

@ApiStatus.Internal
public final class NeoForgeProfileProviderImpl implements ProfileProvider {
    public static final NeoForgeProfileProviderImpl INSTANCE = new NeoForgeProfileProviderImpl();

    private NeoForgeProfileProviderImpl() {}

    @Override
    public @NotNull Optional<ModProfile> modOptional(@NotNull String id) {
        return ModList.get().getModContainerById(id).map(NeoForgeModProfileImpl::from);
    }
}
