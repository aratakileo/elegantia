package io.github.aratakileo.elegantia.client;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.function.BiConsumer;

public final class ObjectRenderTypes {
    private final static HashMap<Block, RenderType> TYPE_TO_BLOCK = new HashMap<>();
    private final static HashMap<Item, RenderType> TYPE_TO_ITEM = new HashMap<>();
    private final static HashMap<Fluid, RenderType> TYPE_TO_FLUID = new HashMap<>();

    private static BiConsumer<Block, RenderType> BLOCK_HANDLER = TYPE_TO_BLOCK::put;
    private static BiConsumer<Item, RenderType> ITEM_HANDLER = TYPE_TO_ITEM::put;
    private static BiConsumer<Fluid, RenderType> FLUID_HANDLER = TYPE_TO_FLUID::put;

    private ObjectRenderTypes() {}

    public static void putBlock(@NotNull Block block, @NotNull RenderType renderType) {
        BLOCK_HANDLER.accept(block, renderType);
    }

    public static void putItem(@NotNull Item item, @NotNull RenderType renderType) {
        ITEM_HANDLER.accept(item, renderType);
    }

    public static void putFluid(@NotNull Fluid fluid, @NotNull RenderType renderType) {
        FLUID_HANDLER.accept(fluid, renderType);
    }

    public static void init(
            @NotNull BiConsumer<Block, RenderType> blockHandler,
            @NotNull BiConsumer<Fluid, RenderType> fluidHandler
    ) {
        final var itemHandler = (BiConsumer<Item, RenderType>) (item, renderType) -> blockHandler.accept(
                Block.byItem(item),
                renderType
        );

        TYPE_TO_BLOCK.forEach(blockHandler);
        TYPE_TO_ITEM.forEach(itemHandler);
        TYPE_TO_FLUID.forEach(fluidHandler);

        BLOCK_HANDLER = blockHandler;
        ITEM_HANDLER = itemHandler;
        FLUID_HANDLER = fluidHandler;
    }
}
