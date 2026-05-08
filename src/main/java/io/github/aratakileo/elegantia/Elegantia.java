package io.github.aratakileo.elegantia;

import io.github.aratakileo.elegantia.core.Namespace;
import io.github.aratakileo.elegantia.core.Platform;
import io.github.aratakileo.elegantia.updatechecker.ModrinthUpdateChecker;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;

public class Elegantia implements ClientModInitializer {
    public final static Logger LOGGER = Namespace.ELEGANTIA.getLogger();

    @Override
    public void onInitializeClient() {
        final var elegantiaMod = Namespace.ELEGANTIA.getModOrThrow();
        final var modrinthResponse = ModrinthUpdateChecker.of(Namespace.ELEGANTIA).check();

        LOGGER.info(
                "Elegantia v{} ({}) powered by {}, ran on {} for minecraft v{}",
                elegantiaMod.version(),
                modrinthResponse.isUpdateAvailable() ? "outdated" : "up to date",
                elegantiaMod.kernelPlatform().name().toLowerCase(),
                Platform.getCurrent().name().toLowerCase(),
                Platform.getMinecraftVersion()
        );
    }
}
