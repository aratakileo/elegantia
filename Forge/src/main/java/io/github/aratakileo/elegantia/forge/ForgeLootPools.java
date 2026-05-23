package io.github.aratakileo.elegantia.forge;

import io.github.aratakileo.elegantia.util.Strings;
import net.minecraft.world.level.storage.loot.LootPool;
import org.jetbrains.annotations.NotNull;

public final class ForgeLootPools {
    private static long counter = 0;

    private ForgeLootPools() {}

    public static @NotNull LootPool.Builder builder() {
        return LootPool.lootPool().name(Strings.format("elegantia_forge_pool_{}", ++counter));
    }
}
