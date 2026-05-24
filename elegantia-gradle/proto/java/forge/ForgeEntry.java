package ${package_name};

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@Mod("${mod_id}")
public final class ForgeEntry {
    public ForgeEntry(@NotNull FMLJavaModLoadingContext context) {
        ForgeInitialisation.init(context);
        ${mod_prelaunch_entry}
        ${mod_common_entry}
        FMLLoadCompleteEvent.getBus(context.getModBusGroup()).addListener(this::setup);
    }

    private void setup(final @NotNull FMLLoadCompleteEvent event) {
        if (isClient()) ${mod_client_entry}
        else ${mod_server_entry}
    }

    private static boolean isClient() {
        return FMLEnvironment.dist.isClient();
    }
}