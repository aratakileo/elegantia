package io.github.aratakileo.elegantia.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
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
        ).orElseThrow();

        return ModInfo.isModLoaded("quilt_loader") ? QUILT : FABRIC;
    }

    public static @NotNull String getMinecraftVersion() {
        return SharedConstants.getCurrentVersion().getName();
    }

    public static @NotNull Environment getEnvironment() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT
                ? Environment.CLIENT : Environment.SERVER;
    }

    public enum Environment {
        CLIENT,
        SERVER;

        public boolean isClient() {
            return this == CLIENT;
        }

        public boolean isServer() {
            return  this == SERVER;
        }
    }
}
