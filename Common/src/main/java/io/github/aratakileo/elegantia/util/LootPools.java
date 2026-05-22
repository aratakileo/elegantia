package io.github.aratakileo.elegantia.util;

import io.github.aratakileo.elegantia.core.Result;
import io.github.aratakileo.elegantia.core.environment.Loader;
import io.github.aratakileo.elegantia.core.environment.Origin;
import net.minecraft.world.level.storage.loot.LootPool;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

public final class LootPools {
    private static Method forgeBasedPoolCreator = null;

    private LootPools() {}

    public static @NotNull LootPool.Builder builder() {
        if (Loader.current().forgeBased() && forgeBasedPoolCreator == null)
            forgeBasedPoolCreator = Classes.load(Origin.ELEGANTIA, Strings.format(
                    "io.github.aratakileo.elegantia.{}.{}LootPools",
                    Loader.current().name().toLowerCase(),
                    Loader.current().displayName()
            )).map(clazz -> clazz.getMethod("builder")).unwrap();

        if (forgeBasedPoolCreator == null) return LootPool.lootPool();

        return Result.fromFactory(() -> (LootPool.Builder) forgeBasedPoolCreator.invoke(null)).unwrap();
    }
}
