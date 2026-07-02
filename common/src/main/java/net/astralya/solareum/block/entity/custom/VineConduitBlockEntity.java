package net.astralya.solareum.block.entity.custom;

import net.astralya.solareum.block.entity.ModBlockEntityTypes;
import net.astralya.solareum.energy.LuxBlockProvider;
import net.astralya.solareum.energy.LuxSideMode;
import net.astralya.solareum.energy.LuxSideModeStorage;
import net.astralya.solareum.energy.LuxStorage;
import net.astralya.solareum.energy.LuxTransferUtil;
import net.astralya.solareum.energy.SimpleLuxStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class VineConduitBlockEntity extends BlockEntity implements LuxBlockProvider {
    private static final int CAPACITY = 500;
    private static final int MAX_TRANSFER = 50;
    private static final LuxSideMode DEFAULT_SIDE_MODE = LuxSideMode.BOTH;

    private final SimpleLuxStorage luxStorage = new SimpleLuxStorage(CAPACITY, MAX_TRANSFER, MAX_TRANSFER);
    private final LuxSideMode[] sideModes = LuxSideModeStorage.create(DEFAULT_SIDE_MODE);

    public VineConduitBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.VINE_CONDUIT.get(), pos, state);
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

    public static void serverTick(
            Level level, BlockPos pos, BlockState state, VineConduitBlockEntity blockEntity) {
        if (blockEntity.transferToAdjacentReceivers(level, pos) > 0) {
            blockEntity.setChanged();
        }
    }

    private int transferToAdjacentReceivers(Level level, BlockPos pos) {
        int remaining = MAX_TRANSFER;
        int transferred = 0;

        for (Direction direction : Direction.values()) {
            if (remaining <= 0 || luxStorage.getStored() <= 0) {
                break;
            }

            BlockEntity blockEntity = level.getBlockEntity(pos.relative(direction));
            if (blockEntity instanceof VineConduitBlockEntity vineConduit && vineConduit != this) {
                int balanceAmount = getConduitBalanceAmount(vineConduit, remaining);
                if (balanceAmount <= 0) {
                    continue;
                }

                int moved = LuxTransferUtil.transfer(
                        this, direction, vineConduit, direction.getOpposite(), balanceAmount);
                if (moved > 0) {
                    vineConduit.setChanged();
                    remaining -= moved;
                    transferred += moved;
                }
                continue;
            }

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

    private int getConduitBalanceAmount(VineConduitBlockEntity target, int maxAmount) {
        int difference = luxStorage.getStored() - target.getStoredLux();
        if (difference <= 1) {
            return 0;
        }

        return Math.min(maxAmount, difference / 2);
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
