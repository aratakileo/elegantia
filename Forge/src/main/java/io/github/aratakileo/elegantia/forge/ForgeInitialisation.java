package io.github.aratakileo.elegantia.forge;

import io.github.aratakileo.elegantia.common.environment.Loader;
import io.github.aratakileo.elegantia.forge.profile.ForgeProfileProvider;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class ForgeInitialisation {
    public final static String MOD_ID = "elegantia";

    private ForgeInitialisation() {}

    public static void init(@NotNull FMLJavaModLoadingContext context) {
        Loader.setProfileProvider(ForgeProfileProvider.INSTANCE);
        RegistryServiceImpl.impl(context);
    }
}
