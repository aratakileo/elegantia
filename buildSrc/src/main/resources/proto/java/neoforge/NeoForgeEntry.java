package ${package_name};

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Mod("${mod_id}")
public class NeoForgeEntry {
    public NeoForgeEntry(@NotNull ModContainer container) {
        ${mod_prelaunch_entry}
        ${mod_common_entry}

        Objects.requireNonNull(container.getEventBus()).addListener(this::setup);
    }

    private void setup(final @NotNull FMLLoadCompleteEvent event) {
        if (isClient()) ${mod_client_entry}
        else ${mod_server_entry}
    }

    private static boolean isClient() {
        return FMLEnvironment.getDist().isClient();
    }
}