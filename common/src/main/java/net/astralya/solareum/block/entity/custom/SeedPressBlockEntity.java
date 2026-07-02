package net.astralya.solareum.block.entity.custom;

import net.astralya.solareum.block.entity.ModBlockEntityTypes;
import net.astralya.solareum.energy.LuxBlockProvider;
import net.astralya.solareum.energy.LuxSideMode;
import net.astralya.solareum.energy.LuxSideModeStorage;
import net.astralya.solareum.energy.LuxStorage;
import net.astralya.solareum.energy.SimpleLuxStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class SeedPressBlockEntity extends BlockEntity implements LuxBlockProvider {
    private static final int CAPACITY = 1_000;
    private static final int MAX_RECEIVE = 50;
    private static final LuxSideMode DEFAULT_SIDE_MODE = LuxSideMode.INPUT;

    private final SimpleLuxStorage luxStorage = new SimpleLuxStorage(CAPACITY, MAX_RECEIVE, 0);
    private final LuxSideMode[] sideModes = LuxSideModeStorage.create(DEFAULT_SIDE_MODE);

    public SeedPressBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.SEED_PRESS.get(), pos, state);
    }

    public SimpleLuxStorage getLuxStorage() {
        return luxStorage;
    }

    @Override
    public LuxStorage getLuxStorage(Direction side) {
        return luxStorage;
    }

    @Override
    public LuxSideMode getSideMode(Direction side) {
        return LuxSideModeStorage.get(sideModes, side);
    }

    @Override
    public void setSideMode(Direction side, LuxSideMode mode) {
        LuxSideModeStorage.set(sideModes, side, mode);
    }

    public int getStoredLux() {
        return luxStorage.getStored();
    }

    public int getCapacity() {
        return luxStorage.getCapacity();
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        luxStorage.load(tag);
        LuxSideModeStorage.load(tag, sideModes, DEFAULT_SIDE_MODE);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        luxStorage.save(tag);
        LuxSideModeStorage.save(tag, sideModes);
    }
}
