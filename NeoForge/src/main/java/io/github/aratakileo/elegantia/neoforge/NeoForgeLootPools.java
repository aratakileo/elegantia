package io.github.aratakileo.elegantia.neoforge;

import io.github.aratakileo.elegantia.core.util.Strings;
import net.minecraft.world.level.storage.loot.LootPool;
import org.jetbrains.annotations.NotNull;

public final class NeoForgeLootPools {
    private static long counter = 0;

    private NeoForgeLootPools() {}

    public static @NotNull LootPool.Builder builder() {
        return LootPool.lootPool().name(Strings.format("elegantia_neoforge_pool_{}", ++counter));
    }
}
