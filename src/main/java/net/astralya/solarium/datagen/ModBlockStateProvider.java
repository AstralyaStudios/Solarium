package net.astralya.solarium.datagen;

import net.astralya.solarium.SolariumMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModBlockStateProvider extends BlockStateProvider {

    private String blockName(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block).getPath();
    }

    public ResourceLocation resourceBlock(String path) {
        return ResourceLocation.fromNamespaceAndPath(SolariumMod.MODID, "block/" + path);
    }

    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, SolariumMod.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {


    }
}
