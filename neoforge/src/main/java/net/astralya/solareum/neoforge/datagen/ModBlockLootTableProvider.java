package net.astralya.solareum.neoforge.datagen;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.astralya.solareum.block.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

public final class ModBlockLootTableProvider extends LootTableProvider {
    public ModBlockLootTableProvider(
            PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(
                output,
                Set.of(),
                List.of(new SubProviderEntry(ModBlockLootSubProvider::new, LootContextParamSets.BLOCK)),
                registries);
    }

    private static final class ModBlockLootSubProvider extends BlockLootSubProvider {
        private static final List<Block> KNOWN_BLOCKS =
                List.of(
                        ModBlocks.HAND_CRANK_PRESS.get(),
                        ModBlocks.LEAF_PANEL.get(),
                        ModBlocks.MOSS_CAPACITOR.get(),
                        ModBlocks.VINE_CONDUIT.get(),
                        ModBlocks.SEED_PRESS.get());

        protected ModBlockLootSubProvider(HolderLookup.Provider registries) {
            super(Set.<Item>of(), FeatureFlags.REGISTRY.allFlags(), registries);
        }

        @Override
        protected void generate() {
            dropSelf(ModBlocks.HAND_CRANK_PRESS.get());
            dropSelf(ModBlocks.LEAF_PANEL.get());
            dropSelf(ModBlocks.MOSS_CAPACITOR.get());
            dropSelf(ModBlocks.VINE_CONDUIT.get());
            dropSelf(ModBlocks.SEED_PRESS.get());
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return KNOWN_BLOCKS;
        }
    }
}
