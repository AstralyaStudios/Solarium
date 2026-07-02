package net.astralya.solareum.energy;

import net.minecraft.nbt.CompoundTag;

public final class SimpleLuxStorage implements LuxStorage {
    private static final String STORED_KEY = "Lux";

    private final int capacity;
    private final int maxReceive;
    private final int maxExtract;
    private int stored;

    public SimpleLuxStorage(int capacity, int maxReceive, int maxExtract) {
        this(capacity, maxReceive, maxExtract, 0);
    }

    public SimpleLuxStorage(int capacity, int maxReceive, int maxExtract, int stored) {
        this.capacity = Math.max(0, capacity);
        this.maxReceive = Math.max(0, maxReceive);
        this.maxExtract = Math.max(0, maxExtract);
        setStored(stored);
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public int getStored() {
        return stored;
    }

    @Override
    public int getMaxReceive() {
        return maxReceive;
    }

    @Override
    public int getMaxExtract() {
        return maxExtract;
    }

    @Override
    public int receive(int amount, boolean simulate) {
        int received = Math.min(Math.max(0, amount), Math.min(maxReceive, capacity - stored));
        if (!simulate) {
            stored += received;
        }
        return received;
    }

    @Override
    public int extract(int amount, boolean simulate) {
        int extracted = Math.min(Math.max(0, amount), Math.min(maxExtract, stored));
        if (!simulate) {
            stored -= extracted;
        }
        return extracted;
    }

    @Override
    public void setStored(int amount) {
        stored = Math.max(0, Math.min(capacity, amount));
    }

    public void load(CompoundTag tag) {
        setStored(tag.getInt(STORED_KEY));
    }

    public void save(CompoundTag tag) {
        tag.putInt(STORED_KEY, stored);
    }
}
