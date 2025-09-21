package net.astralya.solarium.datagen;

import net.astralya.solarium.SolariumMod;
import net.astralya.solarium.block.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {

    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, SolariumMod.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        addMiningTags();
    }

    private void addMiningTags() {
        // Pickaxe Mineable Blocks
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .add(ModBlocks.PHOTOSMELTER.get())
                    .add(ModBlocks.SUNFLOWER_GENERATOR.get());

        // Tools Requirements
        tag(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.PHOTOSMELTER.get())
                .add(ModBlocks.SUNFLOWER_GENERATOR.get());
    }
}
