package io.github.aratakileo.elegantia.forge;

import io.github.aratakileo.elegantia.core.environment.Loader;
import io.github.aratakileo.elegantia.forge.profile.ForgeProfileProvider;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.NotNull;

public final class ForgeInitialisation {
    public final static String MOD_ID = "elegantia";

    private ForgeInitialisation() {}

    public static void init(@NotNull FMLJavaModLoadingContext context) {
        Loader.setProfileProvider(ForgeProfileProvider.INSTANCE);
        RegistryServiceImpl.impl(context);
    }
}
