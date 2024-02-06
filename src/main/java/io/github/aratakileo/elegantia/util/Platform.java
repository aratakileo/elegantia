package io.github.aratakileo.elegantia.util;

import net.minecraft.SharedConstants;
import org.jetbrains.annotations.NotNull;

public enum Platform {
    FABRIC,
    QUILT,
    FORGE;

    public static @NotNull Platform getCurrentPlatform() {
        if (ModInfo.isModLoaded("forge")) return FORGE;

        return ModInfo.isModLoaded("quilt_loader") ? QUILT : FABRIC;
    }

    public static @NotNull String getCurrentMinecraftVersion() {
        return SharedConstants.getCurrentVersion().getName();
    }
}
