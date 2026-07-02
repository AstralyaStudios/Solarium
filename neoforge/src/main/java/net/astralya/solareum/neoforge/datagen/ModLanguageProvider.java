package net.astralya.solareum.neoforge.datagen;

import net.astralya.solareum.Solareum;
import net.astralya.solareum.item.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public final class ModLanguageProvider extends LanguageProvider {
    public ModLanguageProvider(PackOutput output) {
        super(output, Solareum.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add(ModItems.SOLAREUM_EMBLEM.get(), "Solareum Emblem");
        add(ModItems.HAND_CRANK_PRESS.get(), "Hand-Crank Press");
        add(ModItems.LEAF_PANEL.get(), "Leaf Panel");
        add(ModItems.MOSS_CAPACITOR.get(), "Moss Capacitor");
        add(ModItems.VINE_CONDUIT.get(), "Vine Conduit");
        add(ModItems.SEED_PRESS.get(), "Seed Press");
        add(ModItems.BIO_FIBER.get(), "Bio-Fiber");
        add(ModItems.SOLAR_JOURNAL.get(), "Solar Journal");
        add(ModItems.LUX_METER.get(), "Lux Meter");
        add(ModItems.WRENCH.get(), "Wrench");
        add("itemGroup.solareum", "Solareum");
    }
}
