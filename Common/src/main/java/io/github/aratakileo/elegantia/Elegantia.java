package io.github.aratakileo.elegantia;

import io.github.aratakileo.elegantia.common.environment.Loader;
import io.github.aratakileo.elegantia.common.environment.Origin;
import io.github.aratakileo.elegantia.core.entrypoint.EntryInitializer;
import io.github.aratakileo.elegantia.core.entrypoint.ModEntry;
import io.github.aratakileo.elegantia.core.entrypoint.EntryPoint;

@ModEntry(value = EntryPoint.PRELAUNCH)
public class Elegantia {
    @EntryInitializer
    public static void init() {
        Origin.ELEGANTIA.logger().info(
                "Loading Elegantia v{} on the {} environment",
                Origin.ELEGANTIA.unwrapProfile().versionName(),
                Loader.environment()
        );
    }
}
