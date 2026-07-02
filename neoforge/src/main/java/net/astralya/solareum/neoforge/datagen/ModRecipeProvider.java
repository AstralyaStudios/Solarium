package net.astralya.solareum.neoforge.datagen;

import java.util.concurrent.CompletableFuture;
import net.astralya.solareum.item.ModItems;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

public final class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(
            PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        buildCraftingRecipes(recipeOutput);
    }

    private void buildCraftingRecipes(RecipeOutput recipeOutput) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, ModItems.SOLAR_JOURNAL.get())
                .requires(Items.BOOK)
                .requires(ModItems.BIO_FIBER.get())
                .unlockedBy(
                        "has_bio_fiber",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.BIO_FIBER.get()).build()))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BIO_FIBER.get())
                .requires(Items.BAMBOO)
                .unlockedBy(
                        "has_bamboo",
                        inventoryTrigger(ItemPredicate.Builder.item().of(Items.BAMBOO).build()))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BIO_FIBER.get(), 2)
                .requires(Items.VINE)
                .unlockedBy(
                        "has_vine",
                        inventoryTrigger(ItemPredicate.Builder.item().of(Items.VINE).build()))
                .save(recipeOutput, "solareum:bio_fiber_from_vines");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BIO_FIBER.get())
                .requires(ItemTags.LEAVES)
                .unlockedBy(
                        "has_leaves",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ItemTags.LEAVES).build()))
                .save(recipeOutput, "solareum:bio_fiber_from_leaves");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, ModItems.LEAF_PANEL.get())
                .requires(ModItems.BIO_FIBER.get())
                .requires(ItemTags.LEAVES)
                .requires(Items.GLASS_PANE)
                .unlockedBy(
                        "has_bio_fiber",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.BIO_FIBER.get()).build()))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, ModItems.MOSS_CAPACITOR.get())
                .requires(ModItems.LEAF_PANEL.get())
                .requires(ModItems.BIO_FIBER.get())
                .requires(Items.MOSS_BLOCK)
                .unlockedBy(
                        "has_leaf_panel",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.LEAF_PANEL.get()).build()))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, ModItems.VINE_CONDUIT.get(), 4)
                .requires(ModItems.BIO_FIBER.get())
                .requires(Items.VINE)
                .unlockedBy(
                        "has_bio_fiber",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.BIO_FIBER.get()).build()))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, ModItems.SEED_PRESS.get())
                .requires(ModItems.HAND_CRANK_PRESS.get())
                .requires(ModItems.LEAF_PANEL.get())
                .requires(ModItems.BIO_FIBER.get())
                .unlockedBy(
                        "has_hand_crank_press",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.HAND_CRANK_PRESS.get()).build()))
                .save(recipeOutput);
    }
}
