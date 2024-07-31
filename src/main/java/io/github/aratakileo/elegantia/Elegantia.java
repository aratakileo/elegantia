package io.github.aratakileo.elegantia;

import io.github.aratakileo.elegantia.util.ModInfo;
import io.github.aratakileo.elegantia.util.Namespace;
import io.github.aratakileo.elegantia.util.Platform;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;

public class Elegantia implements ClientModInitializer {
    public final static Logger LOGGER = Namespace.ELEGANTIA.getLogger();

    @Override
    public void onInitializeClient() {
        ElegantiaConfig.instance = ElegantiaConfig.init(ElegantiaConfig.class, Namespace.ELEGANTIA);

        LOGGER.info(
                "Elegantia kernel platform: {}, loading platform: {}",
                ModInfo.getKernelPlatform(Namespace.ELEGANTIA).orElseThrow(),
                Platform.getCurrent()
        );
    }
}
