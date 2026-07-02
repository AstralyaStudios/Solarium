package net.astralya.solareum.neoforge.energy;

import net.astralya.solareum.block.entity.ModBlockEntityTypes;
import net.astralya.solareum.energy.LuxBlockProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.IEnergyStorage;

public final class NeoForgeLuxCapabilities {
    private NeoForgeLuxCapabilities() {
    }

    public static void register(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntityTypes.LEAF_PANEL.get(),
                NeoForgeLuxCapabilities::getEnergyStorage);
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntityTypes.MOSS_CAPACITOR.get(),
                NeoForgeLuxCapabilities::getEnergyStorage);
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntityTypes.VINE_CONDUIT.get(),
                NeoForgeLuxCapabilities::getEnergyStorage);
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntityTypes.SEED_PRESS.get(),
                NeoForgeLuxCapabilities::getEnergyStorage);
    }

    private static IEnergyStorage getEnergyStorage(BlockEntity blockEntity, Direction side) {
        if (side == null || !(blockEntity instanceof LuxBlockProvider provider)) {
            return null;
        }

        return new NeoForgeLuxEnergyStorage(provider, side);
    }
}
