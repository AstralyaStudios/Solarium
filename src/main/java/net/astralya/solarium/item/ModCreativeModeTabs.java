package net.astralya.solarium.item;

import net.astralya.solarium.SolariumMod;
import net.astralya.solarium.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SolariumMod.MODID);

    public static final Supplier<CreativeModeTab> SOLARIUM_TAB = CREATIVE_MODE_TABS.register("solarium_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.solarium"))
            .icon(() -> new ItemStack(ModItems.WRENCH.get()))
            .displayItems((itemDisplayParameters, output) -> {

                // Tools & Utilities
                output.accept(ModItems.WRENCH.get());
                output.accept(ModItems.ENERGY_METER.get());
                output.accept(ModItems.SOLARIUM_DUST.get());
                output.accept(ModItems.SOLARIUM_INGOT.get());
                output.accept(ModBlocks.BIO_CONDUIT.get());

                // Energy Generator
                output.accept(ModBlocks.SUNFLOWER_GENERATOR);

                // Machine Blocks
                // output.accept(ModBlocks.BIOMASS_GENERATOR);
                output.accept(ModBlocks.PHOTOSMELTER);
                // output.accept(ModBlocks.GENERADOES);
                // output.accept(ModBlocks.LED_PANEL.get());

                // Misc Items
                output.accept(ModItems.ETERNAL_NIGHT_MUSIC_DISC.get());
            })
            .build());

    public static void register (IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
