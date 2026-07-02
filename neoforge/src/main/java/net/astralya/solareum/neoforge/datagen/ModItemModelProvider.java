package net.astralya.solareum.neoforge.datagen;

import net.astralya.solareum.Solareum;
import net.astralya.solareum.item.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public final class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Solareum.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ModItems.SOLAREUM_EMBLEM.get());
        basicItem(ModItems.BIO_FIBER.get());
        basicItem(ModItems.SOLAR_JOURNAL.get());
        basicItem(ModItems.LUX_METER.get());
        basicItem(ModItems.WRENCH.get());
    }
}
