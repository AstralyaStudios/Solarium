package net.astralya.solarium.item;

import net.astralya.solarium.SolariumMod;
import net.astralya.solarium.item.custom.EnergyMeterItem;
import net.astralya.solarium.sound.ModSoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;



public class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SolariumMod.MODID);

    // Tools & Utilities
    public static final DeferredItem<Item> WRENCH = ITEMS.registerItem("wrench",
            Item::new, new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> ENERGY_METER = ITEMS.registerItem("energy_meter",
            EnergyMeterItem::new, new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));

    // Misc Items
    public static final DeferredItem<Item> ETERNAL_NIGHT_MUSIC_DISC = ITEMS.registerItem("eternal_night_music_disc",
            properties -> new Item(properties.jukeboxPlayable(ModSoundEvents.ETERNAL_NIGHT_KEY)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}


