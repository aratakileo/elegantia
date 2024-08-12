package io.github.aratakileo.elegantia.world;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class MultyOutputRecipe<T extends RecipeInput> implements Recipe<T> {
    @Override
    public abstract boolean matches(@NotNull T recipeInput, @NotNull Level level);

    public @NotNull List<ItemStack> assembles(@NotNull T recipeInput, HolderLookup.Provider provider) {
        return List.copyOf(getResultItems());
    }

    @Deprecated
    @Override
    public final @NotNull ItemStack assemble(@NotNull T recipeInput, HolderLookup.Provider provider) {
        throw new RuntimeException("Do not use this method!");
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    public abstract @NotNull List<ItemStack> getResultItems();

    @Deprecated
    @Override
    public final @NotNull ItemStack getResultItem(HolderLookup.Provider provider) {
        throw new RuntimeException("Do not use this method!");
    }
}
