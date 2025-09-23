package net.astralya.solarium.datagen;

import net.astralya.solarium.block.ModBlocks;
import net.astralya.solarium.item.ModItems;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
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

        // Shaped Recipes
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.WRENCH.get())
                .pattern(" S ")
                .pattern(" SS")
                .pattern("S  ")
                .define('S', Items.COPPER_INGOT)
                .unlockedBy("has_copper_ingot", inventoryTrigger(ItemPredicate.Builder.item().of(Items.COPPER_INGOT).build()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, ModBlocks.SUNFLOWER_GENERATOR.get())
                .pattern("GGG")
                .pattern("SDS")
                .pattern("IRI")
                .define('G', Blocks.GLASS)
                .define('S', Items.COPPER_INGOT)
                .define('D', Items.DIAMOND)
                .define('I', Items.IRON_INGOT)
                .define('R', Items.REDSTONE)
                .unlockedBy("has_glass", inventoryTrigger(ItemPredicate.Builder.item().of(Blocks.GLASS).build()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, ModBlocks.PHOTOSMELTER.get())
                .pattern("RGR")
                .pattern("GGG")
                .pattern("RGR")
                .define('G', Blocks.SMOOTH_STONE)
                .define('R', Items.COPPER_INGOT)
                .unlockedBy("has_smooth_stone", inventoryTrigger(ItemPredicate.Builder.item().of(Blocks.SMOOTH_STONE).build()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, ModBlocks.BIO_CONDUIT.get(), 2)
                .pattern("VVV")
                .pattern("RVR")
                .pattern("VVV")
                .define('V', Blocks.VINE)
                .define('R', ModItems.SOLARIUM_INGOT)
                .unlockedBy("has_solarium_ingot", inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.SOLARIUM_INGOT).build()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, ModItems.ENERGY_METER.get())
                .pattern(" T ")
                .pattern("RGR")
                .pattern(" R ")
                .define('T', Blocks.GLASS_PANE)
                .define('G', ModItems.SOLARIUM_INGOT)
                .define('R', Items.IRON_INGOT)
                .unlockedBy("has_solarium_ingot", inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.SOLARIUM_INGOT).build()))
                .save(recipeOutput);

        // Shapeless Recipes
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.SOLARIUM_DUST.get())
                .requires(Items.GLOWSTONE_DUST)
                .requires(Items.REDSTONE)
                .requires(Items.GREEN_DYE)
                .unlockedBy("has_glowstone", inventoryTrigger(ItemPredicate.Builder.item().of(Items.GLOWSTONE_DUST).build()))
                .save(recipeOutput);

        // Blasting Recipes
        oreBlasting(recipeOutput, List.of(ModItems.SOLARIUM_DUST), RecipeCategory.MISC, ModItems.SOLARIUM_INGOT, 0.25f, 200, "solarium_ingot");
        oreSmelting(recipeOutput, List.of(ModItems.SOLARIUM_DUST), RecipeCategory.MISC, ModItems.SOLARIUM_INGOT, 0.25f, 200, "solarium_ingot");

        super.buildRecipes(recipeOutput);
    }
}

