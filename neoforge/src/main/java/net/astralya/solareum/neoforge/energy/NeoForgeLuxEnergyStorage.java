package net.astralya.solareum.neoforge.energy;

import net.astralya.solareum.energy.LuxBlockProvider;
import net.astralya.solareum.energy.LuxStorage;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.energy.IEnergyStorage;

public final class NeoForgeLuxEnergyStorage implements IEnergyStorage {
    private final LuxBlockProvider provider;
    private final Direction side;

    public NeoForgeLuxEnergyStorage(LuxBlockProvider provider, Direction side) {
        this.provider = provider;
        this.side = side;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive()) {
            return 0;
        }

        return storage().receive(maxReceive, simulate);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!canExtract()) {
            return 0;
        }

        return storage().extract(maxExtract, simulate);
    }

    @Override
    public int getEnergyStored() {
        return storage().getStored();
    }

    @Override
    public int getMaxEnergyStored() {
        return storage().getCapacity();
    }

    @Override
    public boolean canExtract() {
        return provider.getSideMode(side).allowsOutput() && storage().getMaxExtract() > 0;
    }

    @Override
    public boolean canReceive() {
        return provider.getSideMode(side).allowsInput() && storage().getMaxReceive() > 0;
    }

    private LuxStorage storage() {
        return provider.getLuxStorage(side);
    }
}
