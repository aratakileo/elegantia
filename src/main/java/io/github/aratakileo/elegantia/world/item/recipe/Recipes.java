package io.github.aratakileo.elegantia.world.item.recipe;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public final class Recipes {
    private static final HashMap<RecipeType<?>, List<Recipe<?>>> recipeBuffer = new HashMap<>();
    private static final HashMap<RecipeType<?>, List<ItemStack>> resultsBuffer = new HashMap<>();
    private static final HashMap<RecipeType<?>, List<List<ItemStack>>> multyResultsBuffer = new HashMap<>();
    private static final HashMap<RecipeType<?>, List<List<Ingredient>>> ingredientsBuffer = new HashMap<>();
    private static @Nullable Level bufferLevel = null;

    private Recipes() {}

    public static <I extends RecipeInput, T extends Recipe<I>> boolean isResult(
            @NotNull RecipeType<T> recipeType,
            @NotNull Item item
    ) {
        return getResults(recipeType)
                .stream()
                .anyMatch(itemStack -> itemStack.is(item));
    }

    public static <I extends RecipeInput, T extends MultyOutputRecipe<I>> boolean isResult(
            @NotNull RecipeType<T> recipeType,
            int stackIndex,
            @NotNull Item item
    ) {
        return getResults(recipeType, stackIndex)
                .stream()
                .anyMatch(itemStack -> itemStack.is(item));
    }

    public static <I extends RecipeInput, T extends Recipe<I>> boolean isIngredient(
            @NotNull RecipeType<T> recipeType,
            int ingredientIndex,
            @NotNull Item item
    ) {
        return getIngredients(recipeType, ingredientIndex)
                .stream()
                .anyMatch(ingredient -> Arrays.stream(ingredient.getItems()).anyMatch(itemStack -> itemStack.is(item)));
    }

    public static <I extends RecipeInput, T extends MultyOutputRecipe<I>> @NotNull List<ItemStack> getResults(
            @NotNull RecipeType<T> recipeType,
            int stackIndex
    ) {
        if (!multyResultsBuffer.containsKey(recipeType)) {
            final var multyResults = new ArrayList<ArrayList<ItemStack>>();

            for (final var recipe: getRecipes(recipeType)) {
                final var resultStacks = ((MultyOutputRecipe<?>)recipe).getResultItems();

                for (var i = 0; i < resultStacks.size(); i++) {
                    if (i == multyResults.size())
                        multyResults.add(new ArrayList<>());

                    multyResults.get(i).add(resultStacks.get(i));
                }
            }

            multyResultsBuffer.put(
                    recipeType,
                    multyResults.stream().map(list -> list.stream().toList()).toList() // To make lists immutable
            );
        }
        
        return multyResultsBuffer.get(recipeType).get(stackIndex);
    }

    public static <I extends RecipeInput, T extends Recipe<I>> @NotNull List<ItemStack> getResults(
            @NotNull RecipeType<T> recipeType
    ) {
        if (!resultsBuffer.containsKey(recipeType))
            resultsBuffer.put(
                    recipeType,
                    getRecipes(recipeType)
                            .stream()
                            .map(recipe -> recipe.getResultItem(null))
                            .toList()
            );
        
        return resultsBuffer.get(recipeType);
    }

    public static <I extends RecipeInput, T extends Recipe<I>> @NotNull List<Ingredient> getIngredients(
            @NotNull RecipeType<T> recipeType,
            int ingredientIndex
    ) {
        if (!ingredientsBuffer.containsKey(recipeType)) {
            final var multyIngredients = new ArrayList<ArrayList<Ingredient>>();

            for (final var recipe: getRecipes(recipeType)) {
                final var ingredientStacks = recipe.getIngredients();

                if (ingredientStacks.isEmpty())
                    throw new IllegalStateException("Recipe type `%s` returned zero ingredients".formatted(
                            BuiltInRegistries.RECIPE_TYPE.getKey(recipeType)
                    ));

                for (var i = 0; i < ingredientStacks.size(); i++) {
                    if (i == multyIngredients.size())
                        multyIngredients.add(new ArrayList<>());

                    multyIngredients.get(i).add(ingredientStacks.get(i));
                }
            }

            ingredientsBuffer.put(
                    recipeType,
                    multyIngredients.stream().map(list -> list.stream().toList()).toList() // To make lists immutable
            );
        }

        return ingredientsBuffer.get(recipeType).get(ingredientIndex);
    }

    public static <I extends RecipeInput, T extends Recipe<I>> List<Recipe<?>> getRecipes(
            @NotNull RecipeType<T> recipeType
    ) {
        if (!recipeBuffer.containsKey(recipeType)) {
            if (Minecraft.getInstance().level == null)
                throw new IllegalStateException("Recipe type `%s` is not bufferized".formatted(
                        BuiltInRegistries.RECIPE_TYPE.getKey(recipeType)
                ));

            bufferize(Minecraft.getInstance().level, recipeType);
        }

        return recipeBuffer.get(recipeType);
    }

    @SuppressWarnings("unchecked")
    public static <I extends RecipeInput, T extends Recipe<I>> void bufferize(
            @NotNull Level level,
            @NotNull RecipeType<T> recipeType
    ) {
        if (level != bufferLevel) {
            bufferLevel = null;
            recipeBuffer.clear();
            resultsBuffer.clear();
            multyResultsBuffer.clear();
            ingredientsBuffer.clear();
        }

        if (bufferLevel == null)
            bufferLevel = level;

        if (!recipeBuffer.containsKey(recipeType))
            recipeBuffer.put(
                    recipeType,
                    (List<Recipe<?>>) level.getRecipeManager()
                            .getAllRecipesFor(recipeType)
                            .stream()
                            .map(RecipeHolder::value)
                            .toList()
            );
    }
}
