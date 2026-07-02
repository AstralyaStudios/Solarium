package net.astralya.solareum.energy;

public interface LuxStorage {
    int getCapacity();

    int getStored();

    int getMaxReceive();

    int getMaxExtract();

    int receive(int amount, boolean simulate);

    int extract(int amount, boolean simulate);

    void setStored(int amount);
}
