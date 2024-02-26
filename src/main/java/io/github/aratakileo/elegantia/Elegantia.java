package io.github.aratakileo.elegantia;

import io.github.aratakileo.elegantia.util.Platform;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Elegantia implements ClientModInitializer {
    public final static Logger LOGGER = LoggerFactory.getLogger(Elegantia.class);
    public final static String MODID = "elegantia";

    @Override
    public void onInitializeClient() {
        ElegantiaConfig.init(ElegantiaConfig.class, MODID);

        LOGGER.info("Current platform: {}", Platform.getCurrent().name());
    }
}
