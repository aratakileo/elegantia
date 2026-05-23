package ${package_name};

import net.fabricmc.api.DedicatedServerModInitializer;

public class FabricServerEntry implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        ${mod_server_entry}
    }
}