package net.astralya.solarium.datagen;

import net.astralya.solarium.SolariumMod;
import net.astralya.solarium.item.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {

    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, SolariumMod.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        // Basic Item
        basicItem(ModItems.ETERNAL_NIGHT_MUSIC_DISC.get());
        basicItem(ModItems.SOLARIUM_DUST.get());
        basicItem(ModItems.SOLARIUM_INGOT.get());
        basicItem(ModItems.ENERGY_METER.get());

        // Handheld
        handheldItem(ModItems.WRENCH.get());
    }
}
