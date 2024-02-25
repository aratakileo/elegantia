package io.github.aratakileo.elegantia;

import io.github.aratakileo.elegantia.gui.config.InfluentialAction;
import io.github.aratakileo.elegantia.gui.config.Config;
import io.github.aratakileo.elegantia.gui.config.ConfigEntry;
import io.github.aratakileo.elegantia.util.Platform;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Elegantia implements ClientModInitializer {
    public final static Logger LOGGER = LoggerFactory.getLogger(Elegantia.class);
    public final static String MODID = "elegantia";

    public static class Conf extends Config {
        @ConfigEntry
        public boolean choZaUrodi = false;

        @ConfigEntry
        public boolean choZaUrodiki = true;

        @InfluentialAction
        @ConfigEntry
        public void resetAllValues() {
            setValuesByDefault();
        }
    }

    @Override
    public void onInitializeClient() {
        Config.init(Conf.class, "elegantia");

        LOGGER.info("Current platform: {}", Platform.getCurrent().name());
    }
}
