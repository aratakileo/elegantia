package ${package_name};

import net.fabricmc.api.ClientModInitializer;

public class FabricClientEntry implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ${mod_client_entry}
    }
}