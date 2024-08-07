package io.github.aratakileo.elegantia.util;

import io.github.aratakileo.elegantia.core.Namespace;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
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
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.function.Consumer;

public final class RegistriesUtil {
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
            Registry.register(BuiltInRegistries.ITEM, location, new BlockItem(block, new Item.Properties()));

        return Registry.register(BuiltInRegistries.BLOCK, location, block);
    }

    public static <T extends BlockEntity> @NotNull BlockEntityType<T> registerBlockEntityType(
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

    public static <T extends AbstractContainerMenu, S extends Screen & MenuAccess<T>> @NotNull MenuType<T> registerMenuType(
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

    public static <T extends Recipe<?>> @NotNull RecipeType<T> registerRecipeType(@NotNull ResourceLocation location) {
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

    public static <T extends RecipeSerializer<S>, S extends Recipe<?>> @NotNull T registerRecipeSerializer(
            @NotNull ResourceLocation resourceLocation,
            @NotNull T serializer
    ) {
        return Registry.register(
                BuiltInRegistries.RECIPE_SERIALIZER,
                resourceLocation,
                RecipeSerializer.register(resourceLocation.getPath(), serializer)
        );
    }

    public static @NotNull ResourceKey<CreativeModeTab> registerItemGroupResource(
            @NotNull CreativeModeTab itemGroup,
            @NotNull Namespace namespace
    ) {
        return registerItemGroupResource(itemGroup, namespace.getLocation("item_group"));
    }

    public static @NotNull ResourceKey<CreativeModeTab> registerItemGroupResource(
            @NotNull CreativeModeTab itemGroup,
            @NotNull ResourceLocation location
    ) {
        final var groupKey = ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), location);

        hasDisplayNameOrThrow(itemGroup, location);
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, groupKey, itemGroup);

        return groupKey;
    }

    public static @NotNull ResourceKey<CreativeModeTab> registerItemGroupListener(
            @NotNull ResourceKey<CreativeModeTab> itemGroupKey,
            @NotNull Consumer<CreativeModeTab.Output> listener
    ) {
        ItemGroupEvents.modifyEntriesEvent(itemGroupKey).register(listener::accept);
        return itemGroupKey;
    }

    private static void hasDisplayNameOrThrow(@NotNull CreativeModeTab itemGroup, @NotNull ResourceLocation location) {
        if (itemGroup.getDisplayName().getString().isBlank())
            throw new IllegalStateException("No display name or empty display name set for %s".formatted(location));
    }
}
