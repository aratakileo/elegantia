package ${package_name};

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class FabricPrelaunchEntry implements PreLaunchEntrypoint {
    @Override
    public void onPreLaunch() {
        ${mod_prelaunch_entry}
    }
}