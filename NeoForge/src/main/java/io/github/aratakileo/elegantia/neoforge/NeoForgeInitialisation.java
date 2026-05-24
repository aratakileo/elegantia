package io.github.aratakileo.elegantia.neoforge;

import io.github.aratakileo.elegantia.common.environment.Loader;
import io.github.aratakileo.elegantia.neoforge.profile.NeoForgeProfileProvider;
import net.neoforged.fml.ModContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class NeoForgeInitialisation {
    public final static String MOD_ID = "elegantia";

    private NeoForgeInitialisation() {}

    public static void init(@NotNull ModContainer context) {
        Loader.setProfileProvider(NeoForgeProfileProvider.INSTANCE);
        RegistryServiceImpl.impl(context);
    }
}
