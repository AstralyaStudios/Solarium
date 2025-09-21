package net.astralya.solarium.datagen;

import net.astralya.solarium.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {

    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput recipeOutput) {

        oreBlasting(recipeOutput, List.of(ModItems.SOLARIUM_DUST), RecipeCategory.MISC, ModItems.SOLARIUM_INGOT, 0.25f, 200, "solarium_ingot");
        oreSmelting(recipeOutput, List.of(ModItems.SOLARIUM_DUST), RecipeCategory.MISC, ModItems.SOLARIUM_INGOT, 0.25f, 200, "solarium_ingot");

        super.buildRecipes(recipeOutput);
    }
}

