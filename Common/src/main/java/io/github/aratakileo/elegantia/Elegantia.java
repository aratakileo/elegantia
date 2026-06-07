package io.github.aratakileo.elegantia;

import io.github.aratakileo.elegantia.common.environment.Loader;
import io.github.aratakileo.elegantia.common.environment.Origin;
import io.github.aratakileo.elegantia.core.entrypoint.EntryInitializer;
import io.github.aratakileo.elegantia.core.entrypoint.ModEntry;
import io.github.aratakileo.elegantia.core.entrypoint.EntryPoint;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@ModEntry(EntryPoint.PRELAUNCH)
public final class Elegantia {
    private Elegantia() {}

    @EntryInitializer
    public static void init() {
        Origin.ELEGANTIA.logger().info(
                "Running Elegantia v{} on the {} {} environment",
                Origin.ELEGANTIA.unwrapProfile().versionName(),
                Loader.environment().name().toLowerCase(),
                Loader.current().displayName()
        );
    }
}
