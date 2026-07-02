package net.astralya.solareum.block.entity.custom;

import net.astralya.solareum.block.entity.ModBlockEntityTypes;
import net.astralya.solareum.energy.LuxBlockProvider;
import net.astralya.solareum.energy.LuxSideMode;
import net.astralya.solareum.energy.LuxSideModeStorage;
import net.astralya.solareum.energy.LuxStorage;
import net.astralya.solareum.energy.LuxTransferUtil;
import net.astralya.solareum.energy.SimpleLuxStorage;
import net.astralya.solareum.util.SunlightCheck;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class LeafPanelBlockEntity extends BlockEntity implements LuxBlockProvider {
    private static final int CAPACITY = 1_000;
    private static final int MAX_EXTRACT = 20;
    private static final int LUX_PER_TICK = 1;
    private static final int MAX_TRANSFER_PER_TICK = 20;
    private static final LuxSideMode DEFAULT_SIDE_MODE = LuxSideMode.OUTPUT;

    private final SimpleLuxStorage luxStorage = new SimpleLuxStorage(CAPACITY, 0, MAX_EXTRACT);
    private final LuxSideMode[] sideModes = LuxSideModeStorage.create(DEFAULT_SIDE_MODE);

    public LeafPanelBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.LEAF_PANEL.get(), pos, state);
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

    public boolean canGenerate() {
        return this.level != null
                && canGenerate(this.level, this.worldPosition)
                && luxStorage.getStored() < luxStorage.getCapacity();
    }

    public static void serverTick(
            Level level, BlockPos pos, BlockState state, LeafPanelBlockEntity blockEntity) {
        boolean changed = false;
        if (blockEntity.canGenerate()) {
            blockEntity.luxStorage.setStored(blockEntity.luxStorage.getStored() + LUX_PER_TICK);
            changed = true;
        }

        changed |= blockEntity.transferToAdjacentReceivers(level, pos) > 0;

        if (changed) {
            blockEntity.setChanged();
        }
    }

    private static boolean canGenerate(Level level, BlockPos pos) {
        SunlightCheck sunlightCheck = new SunlightCheck(level, pos.above());
        sunlightCheck.recheckCanSeeSun();
        return sunlightCheck.getGenerationMultiplier() > 0.0F;
    }

    private int transferToAdjacentReceivers(Level level, BlockPos pos) {
        int remaining = MAX_TRANSFER_PER_TICK;
        int transferred = 0;

        for (Direction direction : Direction.values()) {
            if (remaining <= 0 || luxStorage.getStored() <= 0) {
                break;
            }

            BlockEntity blockEntity = level.getBlockEntity(pos.relative(direction));
            if (!(blockEntity instanceof LuxBlockProvider target)) {
                continue;
            }

            int moved = LuxTransferUtil.transfer(this, direction, target, direction.getOpposite(), remaining);
            if (moved > 0) {
                blockEntity.setChanged();
                remaining -= moved;
                transferred += moved;
            }
        }

        return transferred;
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
