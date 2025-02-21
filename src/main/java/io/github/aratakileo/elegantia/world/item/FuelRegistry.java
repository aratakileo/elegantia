package io.github.aratakileo.elegantia.world.item;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public final class FuelRegistry {
    private FuelRegistry() {}

    private static final Logger LOGGER = LoggerFactory.getLogger(FuelRegistry.class);
    private static final Object2IntMap<ItemLike> ITEM_COOK_TIMES = new Object2IntLinkedOpenHashMap<>();
    private static final Object2IntMap<TagKey<Item>> TAG_COOK_TIMES = new Object2IntLinkedOpenHashMap<>();

    private static boolean canNotAdd(@NotNull String item, int cookTime) {
        if (cookTime > 32767)
            LOGGER.warn("Tried to register an overly high cookTime: {} > 32767! ({})", cookTime, item);

        if (cookTime <= 0) {
            LOGGER.error("Could not be registered an overly low cookTime: {} <= 0! ({})", cookTime, item);
            return true;
        }

        return false;
    }

    public static <T extends ItemLike> @NotNull T add(@NotNull T item, int cookTime) {
        if (canNotAdd(item.toString(), cookTime)) return item;

        ITEM_COOK_TIMES.put(item, cookTime);

        AbstractFurnaceBlockEntity.invalidateCache();

        return item;
    }

    public static @NotNull TagKey<Item> add(@NotNull TagKey<Item> tag, int cookTime) {
        if (canNotAdd(tag.location().toString(), cookTime)) return tag;

        TAG_COOK_TIMES.put(tag, cookTime);

        AbstractFurnaceBlockEntity.invalidateCache();

        return tag;
    }

    public static void applyFuels(@NotNull Map<Item, Integer> cookTimes) {
        for (final var tagEntry: TAG_COOK_TIMES.object2IntEntrySet())
            AbstractFurnaceBlockEntity.add(cookTimes, tagEntry.getKey(), tagEntry.getIntValue());

        for (final var itemEntry: ITEM_COOK_TIMES.object2IntEntrySet())
            AbstractFurnaceBlockEntity.add(cookTimes, itemEntry.getKey(), itemEntry.getIntValue());
    }
}
