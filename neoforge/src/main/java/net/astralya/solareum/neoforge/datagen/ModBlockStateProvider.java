package net.astralya.solareum.neoforge.datagen;

import net.astralya.solareum.Solareum;
import net.astralya.solareum.block.ModBlocks;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public final class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Solareum.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlockWithItem(
                ModBlocks.HAND_CRANK_PRESS.get(),
                models().cubeAll("hand_crank_press", modLoc("block/hand_crank_press")));
        simpleBlockWithItem(
                ModBlocks.LEAF_PANEL.get(),
                models().cubeAll("leaf_panel", modLoc("block/leaf_panel")));
        simpleBlockWithItem(
                ModBlocks.MOSS_CAPACITOR.get(),
                models().cubeAll("moss_capacitor", modLoc("block/moss_capacitor")));
        simpleBlockWithItem(
                ModBlocks.VINE_CONDUIT.get(),
                models().cubeAll("vine_conduit", modLoc("block/vine_conduit")));
        simpleBlockWithItem(
                ModBlocks.SEED_PRESS.get(),
                models().cubeAll("seed_press", modLoc("block/seed_press")));
    }
}
