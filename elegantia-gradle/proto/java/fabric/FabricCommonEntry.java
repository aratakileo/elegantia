package ${package_name};

import net.fabricmc.api.ModInitializer;

public class FabricCommonEntry implements ModInitializer {
    @Override
    public void onInitialize() {
        FabricInitialisation.init();
        ${mod_common_entry}
    }
}