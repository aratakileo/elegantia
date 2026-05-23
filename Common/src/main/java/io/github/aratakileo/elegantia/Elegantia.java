package io.github.aratakileo.elegantia;

import io.github.aratakileo.elegantia.core.environment.Origin;
import io.github.aratakileo.elegantia.core.entrypoint.EntryInitializer;
import io.github.aratakileo.elegantia.core.entrypoint.ModEntry;
import io.github.aratakileo.elegantia.core.entrypoint.EntryPoint;
import io.github.aratakileo.elegantia.util.GameUtils;

@ModEntry(value = EntryPoint.PRELAUNCH)
public class Elegantia {
    @EntryInitializer
    public static void init() {
        Origin.ELEGANTIA.logger().info(
                "Loading Elegantia v{} on the physical {}",
                Origin.ELEGANTIA.unwrapProfile().versionName(),
                GameUtils.PHYSICAL_CLIENT ? "client" : "server"
        );
    }
}
