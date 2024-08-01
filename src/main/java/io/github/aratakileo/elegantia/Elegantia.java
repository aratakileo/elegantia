package io.github.aratakileo.elegantia;

import io.github.aratakileo.elegantia.util.type.Namespace;
import io.github.aratakileo.elegantia.util.type.Platform;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;

public class Elegantia implements ClientModInitializer {
    public final static Logger LOGGER = Namespace.ELEGANTIA.getLogger();

    @Override
    public void onInitializeClient() {
        LOGGER.info(
                "Elegantia kernel platform: {}, loading platform: {}",
                Namespace.ELEGANTIA.getModOrThrow().getKernelPlatform(),
                Platform.getCurrent()
        );
    }
}
