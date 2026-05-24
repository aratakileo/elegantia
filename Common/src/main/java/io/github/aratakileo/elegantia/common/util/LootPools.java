package io.github.aratakileo.elegantia.common.util;

import io.github.aratakileo.elegantia.core.Result;
import io.github.aratakileo.elegantia.common.environment.Loader;
import io.github.aratakileo.elegantia.common.environment.Origin;
import io.github.aratakileo.elegantia.core.util.Classes;
import io.github.aratakileo.elegantia.core.util.Strings;
import net.minecraft.world.level.storage.loot.LootPool;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

public final class LootPools {
    private LootPools() {}

    public static @NotNull LootPool.Builder builder() {
        final var method = ForgeBuilderHolder.INSTANCE;

        if (method == null) return LootPool.lootPool();

        return Result.fromFactory(() -> (LootPool.Builder) method.invoke(null)).unwrap();
    }

    // for the threads race safety
    private static final class ForgeBuilderHolder {
        private static final Method INSTANCE = !Loader.current()
                .forgeBased() ? null : Classes.load(
                        Origin.ELEGANTIA,
                        Strings.format(
                                "io.github.aratakileo.elegantia.{}.{}LootPools",
                                Loader.current().name().toLowerCase(),
                                Loader.current().displayName()
                        )
                ).map(clazz -> clazz.getMethod("builder")).unwrap();
    }
}
