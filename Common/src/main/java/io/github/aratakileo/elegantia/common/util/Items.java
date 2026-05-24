package io.github.aratakileo.elegantia.common.util;

import io.github.aratakileo.elegantia.common.environment.Origin;
import io.github.aratakileo.elegantia.common.resource.RegistryContainer;
import io.github.aratakileo.elegantia.common.resource.RegistryService;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Supplier;

public final class Items {
    private Items() {}

    public static @NotNull RegistryContainer<Item> container(@NotNull Identifier id) {
        return RegistryContainer.create(BuiltInRegistries.ITEM, id);
    }

    public static @NotNull RegistryContainer<Item> container(@NotNull Origin namespace, @NotNull String name) {
        return RegistryContainer.create(BuiltInRegistries.ITEM, namespace, name);
    }

    public static @NotNull ItemStack stack(@NotNull RegistryContainer<Item> item) {
        return new ItemStack(item.unwrap());
    }

    public static @NotNull ItemStack stack(@NotNull RegistryContainer<Item> item, int quantity) {
        return new ItemStack(item.unwrap(), quantity);
    }

    public static @NotNull RegistryContainer<Item> justRegister(
            @NotNull Identifier id,
            @NotNull Supplier<Item> creator
    ) {
        return RegistryService.instance().register(BuiltInRegistries.ITEM, id, creator);
    }

    public static @NotNull RegistryContainer<Item> justRegister(
            @NotNull Identifier id,
            @NotNull Function<Identifier, Item> creator
    ) {
        return justRegister(id, () -> creator.apply(id));
    }

    public static @NotNull RegistryContainer<Item> register(
            @NotNull Identifier id
    ) {
        return justRegister(id, () -> new Item(withId(new Item.Properties(), id)));
    }

    public static @NotNull RegistryContainer<Item> register(
            @NotNull Identifier id,
            @NotNull Function<Item.Properties, Item> creator
    ) {
        return justRegister(id, () -> creator.apply(withId(new Item.Properties(), id)));
    }

    public static @NotNull RegistryContainer<Item> register(
            @NotNull Identifier id,
            @NotNull Supplier<Item.Properties> propertiesCreator,
            @NotNull Function<Item.Properties, Item> creator
    ) {
        return justRegister(id, () -> creator.apply(withId(propertiesCreator.get(), id)));
    }

    public static @NotNull RegistryContainer<Item> register(
            @NotNull Identifier id,
            @NotNull Supplier<Item.Properties> propertiesCreator
    ) {
        return justRegister(id, () -> new Item(withId(propertiesCreator.get(), id)));
    }

    // new

    public static @NotNull RegistryContainer<Item> justRegister(
            @NotNull Origin origin,
            @NotNull String name,
            @NotNull Supplier<Item> creator
    ) {
        return RegistryService.instance().register(BuiltInRegistries.ITEM, origin.id(name), creator);
    }

    public static @NotNull RegistryContainer<Item> justRegister(
            @NotNull Origin origin,
            @NotNull String name,
            @NotNull Function<Identifier, Item> creator
    ) {
        return justRegister(origin.id(name), () -> creator.apply(origin.id(name)));
    }

    public static @NotNull RegistryContainer<Item> register(
            @NotNull Origin origin,
            @NotNull String name
    ) {
        return justRegister(origin.id(name), () -> new Item(withId(new Item.Properties(), origin.id(name))));
    }

    public static @NotNull RegistryContainer<Item> register(

            @NotNull Origin origin,
            @NotNull String name,
            @NotNull Function<Item.Properties, Item> creator
    ) {
        return justRegister(origin.id(name), () -> creator.apply(withId(new Item.Properties(), origin.id(name))));
    }

    public static @NotNull RegistryContainer<Item> register(

            @NotNull Origin origin,
            @NotNull String name,
            @NotNull Supplier<Item.Properties> propertiesCreator,
            @NotNull Function<Item.Properties, Item> creator
    ) {
        return justRegister(origin.id(name), () -> creator.apply(withId(propertiesCreator.get(), origin.id(name))));
    }

    public static @NotNull RegistryContainer<Item> register(
            @NotNull Origin origin,
            @NotNull String name,
            @NotNull Supplier<Item.Properties> propertiesCreator
    ) {
        return justRegister(origin.id(name), () -> new Item(withId(propertiesCreator.get(), origin.id(name))));
    }

    public static @NotNull Item.Properties withId(@NotNull Item.Properties props, @NotNull Identifier id) {
        return props.setId(ResourceKey.create(Registries.ITEM, id));
    }
}
