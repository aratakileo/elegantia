package io.github.aratakileo.elegantia.util;

import io.github.aratakileo.elegantia.Elegantia;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Map;

public final class RegistriesUtil {
    private static final Object2IntMap<ItemLike> ITEM_COOK_TIMES = new Object2IntLinkedOpenHashMap<>();
    private static final Object2IntMap<TagKey<Item>> TAG_COOK_TIMES = new Object2IntLinkedOpenHashMap<>();

    private RegistriesUtil() {}

    public static <T> @NotNull HashSet<T> getElementsFromTag(
            @NotNull DefaultedRegistry<T> registry,
            @NotNull TagKey<T> tag
    ) {
        final var elements = new HashSet<T>();

        for (final var holder: registry.getTagOrEmpty(tag))
            elements.add(holder.value());

        return elements;
    }

    public static @NotNull HashSet<Item> getItemsFromTag(@NotNull TagKey<Item> tag) {
        return getElementsFromTag(BuiltInRegistries.ITEM, tag);
    }

    public static @NotNull HashSet<Block> getBlocksFromTag(@NotNull TagKey<Block> tag) {
        return getElementsFromTag(BuiltInRegistries.BLOCK, tag);
    }

    public static <T> @NotNull TagKey<T> bindTag(
            @NotNull ResourceKey<Registry<T>> registry,
            @NotNull ResourceLocation location
    ) {
        return TagKey.create(registry, location);
    }

    public static @NotNull TagKey<Item> bindItemTag(@NotNull ResourceLocation resourceLocation) {
        return bindTag(Registries.ITEM, resourceLocation);
    }

    public static @NotNull TagKey<Block> bindBlockTag(@NotNull ResourceLocation resourceLocation) {
        return bindTag(Registries.BLOCK, resourceLocation);
    }

    public static @NotNull Item registerItem(@NotNull ResourceLocation location, @NotNull Item item) {
        return Registry.register(BuiltInRegistries.ITEM, location, item);
    }

    public static @NotNull Block registerBlock(@NotNull ResourceLocation location, @NotNull Block block) {
        return registerBlock(location, block, true);
    }

    public static @NotNull Block registerBlock(
            @NotNull ResourceLocation location,
            @NotNull Block block,
            boolean autoRegisterItem
    ) {
        if (autoRegisterItem)
            Registry.register(BuiltInRegistries.ITEM, location, block.asItem());

        return Registry.register(BuiltInRegistries.BLOCK, location, block);
    }

    public static  <T extends BlockEntity> @NotNull BlockEntityType<T> registerBlockEntityType(
            @NotNull ResourceLocation resourceLocation,
            @NotNull Block block,
            @NotNull BlockEntityType.BlockEntitySupplier<T> constructor
    ) {
        return Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                resourceLocation,
                BlockEntityType.Builder.of(constructor, block).build(null)
        );
    }

    public static <T extends AbstractContainerMenu, S extends Screen & MenuAccess<T>> MenuType<T> createMenuType(
            @NotNull ResourceLocation resourceLocation,
            @NotNull MenuType.MenuSupplier<T> menuSupplier,
            @NotNull MenuScreens.ScreenConstructor<T, S> screenConstructor
    ) {
        final var menuType = Registry.register(
                BuiltInRegistries.MENU,
                resourceLocation,
                new MenuType<>(menuSupplier, FeatureFlags.DEFAULT_FLAGS)
        );

        MenuScreens.register(menuType, screenConstructor);

        return menuType;
    }

    public static <T extends Recipe<?>> RecipeType<T> registerRecipeType(@NotNull ResourceLocation location) {
        return Registry.register(
                BuiltInRegistries.RECIPE_TYPE,
                location,
                new RecipeType<T>() {
                    @Override
                    public String toString() {
                        return location.getPath();
                    }
                }
        );
    }

    public static <T extends RecipeSerializer<S>, S extends Recipe<?>> T registerRecipeSerializer(
            @NotNull ResourceLocation resourceLocation,
            @NotNull T serializer
    ) {
        return Registry.register(
                BuiltInRegistries.RECIPE_SERIALIZER,
                resourceLocation,
                RecipeSerializer.register(resourceLocation.getPath(), serializer)
        );
    }

    private static boolean checkCookTime(@NotNull String item, int cookTime) {
        if (cookTime > 32767)
            Elegantia.LOGGER.warn("Tried to register an overly high cookTime: {} > 32767! ({})", cookTime, item);

        if (cookTime <= 0) {
            Elegantia.LOGGER.error("Could not be registered an overly low cookTime: {} <= 0! ({})", cookTime, item);
            return false;
        }

        return true;
    }

    public static <T extends ItemLike> @NotNull T registerAsFuel(@NotNull T item, int cookTime) {
        if (!checkCookTime(item.toString(), cookTime)) return item;

        ITEM_COOK_TIMES.put(item, cookTime);
        AbstractFurnaceBlockEntity.invalidateCache();

        return item;
    }

    public static @NotNull TagKey<Item> registerAsFuel(@NotNull TagKey<Item> tag, int cookTime) {
        if (!checkCookTime(tag.location().toString(), cookTime)) return tag;

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
