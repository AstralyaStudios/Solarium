package net.astralya.solarium.event;

import net.astralya.solarium.SolariumMod;
import net.astralya.solarium.block.entity.ModBlockEntityTypes;
import net.astralya.solarium.block.entity.custom.SunflowerGeneratorBlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(modid = SolariumMod.MODID, bus = EventBusSubscriber.Bus.MOD)

public class ModBusEvents {

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntityTypes.SUNFLOWER_GENERATOR.get(), SunflowerGeneratorBlockEntity::getEnergyStorage);
    }
}
