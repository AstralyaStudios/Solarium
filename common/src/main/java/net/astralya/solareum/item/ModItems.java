package net.astralya.solareum.item;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.astralya.solareum.Solareum;
import net.astralya.solareum.block.ModBlocks;
import net.astralya.solareum.item.custom.LuxMeterItem;
import net.astralya.solareum.item.custom.WrenchItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(Solareum.MOD_ID, Registries.ITEM);

    public static final RegistrySupplier<Item> SOLAREUM_EMBLEM =
            ITEMS.register("solareum_emblem", () -> new Item(defaultProperties()));

    public static final RegistrySupplier<Item> HAND_CRANK_PRESS =
            ITEMS.register(
                    "hand_crank_press",
                    () -> new BlockItem(ModBlocks.HAND_CRANK_PRESS.get(), defaultProperties()));

    public static final RegistrySupplier<Item> LEAF_PANEL =
            ITEMS.register("leaf_panel", () -> new BlockItem(ModBlocks.LEAF_PANEL.get(), defaultProperties()));

    public static final RegistrySupplier<Item> MOSS_CAPACITOR =
            ITEMS.register(
                    "moss_capacitor",
                    () -> new BlockItem(ModBlocks.MOSS_CAPACITOR.get(), defaultProperties()));

    public static final RegistrySupplier<Item> VINE_CONDUIT =
            ITEMS.register("vine_conduit", () -> new BlockItem(ModBlocks.VINE_CONDUIT.get(), defaultProperties()));

    public static final RegistrySupplier<Item> SEED_PRESS =
            ITEMS.register("seed_press", () -> new BlockItem(ModBlocks.SEED_PRESS.get(), defaultProperties()));

    public static final RegistrySupplier<Item> BIO_FIBER =
            ITEMS.register("bio_fiber", () -> new Item(defaultProperties()));

    public static final RegistrySupplier<Item> SOLAR_JOURNAL =
            ITEMS.register("solar_journal", () -> new Item(defaultProperties()));

    public static final RegistrySupplier<Item> LUX_METER =
            ITEMS.register("lux_meter", () -> new LuxMeterItem(defaultProperties()));

    public static final RegistrySupplier<Item> WRENCH =
            ITEMS.register("wrench", () -> new WrenchItem(defaultProperties()));

    private ModItems() {
    }

    private static Item.Properties defaultProperties() {
        return new Item.Properties().arch$tab(ModCreativeModeTabs.SOLAREUM_TAB);
    }

    public static void init() {
        ITEMS.register();
    }
}
