package io.github.aratakileo.elegantia.util;

import net.minecraft.SharedConstants;
import org.jetbrains.annotations.NotNull;

public enum Platform {
    FABRIC,
    QUILT,
    FORGE,
    NEOFORGE;

    public static @NotNull Platform getCurrent() {
        if (ModInfo.isModLoaded("forge")) return ModInfo.getName("forge").map(
                name -> name.equalsIgnoreCase("neoforge") ? NEOFORGE : FORGE
        ).orElse(FORGE);

        return ModInfo.isModLoaded("quilt_loader") ? QUILT : FABRIC;
    }

    public static @NotNull String getMinecraftVersion() {
        return SharedConstants.getCurrentVersion().getName();
    }
}
