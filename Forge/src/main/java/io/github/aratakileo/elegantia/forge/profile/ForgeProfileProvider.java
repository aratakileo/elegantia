package io.github.aratakileo.elegantia.forge.profile;

import io.github.aratakileo.elegantia.core.environment.ModProfile;
import io.github.aratakileo.elegantia.core.environment.ProfileProvider;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class ForgeProfileProvider implements ProfileProvider {
    public static final ForgeProfileProvider INSTANCE = new ForgeProfileProvider();

    private ForgeProfileProvider() {}

    @Override
    public @NotNull Optional<ModProfile> modOptional(@NotNull String id) {
        return ModList.get().getModContainerById(id).map(ForgeModProfile::from);
    }
}
