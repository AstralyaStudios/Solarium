package net.astralya.solarium.block.entity.custom;

import net.astralya.solarium.block.entity.ModBlockEntityTypes;
import net.astralya.solarium.block.entity.energy.ModEnergyStorage;
import net.astralya.solarium.block.entity.energy.ModEnergyUtil;
import net.astralya.solarium.screen.custom.PhotosmelterMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

public class PhotosmelterBlockEntity extends SyncBlockEntity implements MenuProvider {

    public static final int SLOT_INPUT = 0;
    public static final int SLOT_OUTPUT = 1;
    private static final int INV_SIZE = 2;
    private static final int ENERGY_CAPACITY = 10000;
    private static final int MAX_RECEIVE = 500;
    private static final int ENERGY_PER_TICK_LIMIT = 20;
    private static final int ENERGY_PER_ITEM = 1000;

    private int energyPaidThisCycle = 0;

    private final ModEnergyStorage energy = new ModEnergyStorage(ENERGY_CAPACITY, MAX_RECEIVE) {
        @Override
        public void onEnergyChanged() {
            setChanged();
            Level lvl = level;
            if (lvl != null && !lvl.isClientSide()) {
                lvl.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
            }
        }
    };

    private NonNullList<ItemStack> items = NonNullList.withSize(INV_SIZE, ItemStack.EMPTY);
    private int progress;
    private int maxProgress;

    private static final IEnergyStorage EMPTY_ENERGY = new IEnergyStorage() {
        @Override public int receiveEnergy(int maxReceive, boolean simulate) { return 0; }
        @Override public int extractEnergy(int maxExtract, boolean simulate) { return 0; }
        @Override public int getEnergyStored() { return 0; }
        @Override public int getMaxEnergyStored() { return 0; }
        @Override public boolean canExtract() { return false; }
        @Override public boolean canReceive() { return false; }
    };

    private final IEnergyStorage RECEIVE_ONLY = new IEnergyStorage() {
        @Override public int receiveEnergy(int maxReceive, boolean simulate) { return energy.receiveEnergy(maxReceive, simulate); }
        @Override public int extractEnergy(int maxExtract, boolean simulate) { return 0; }
        @Override public int getEnergyStored() { return energy.getEnergyStored(); }
        @Override public int getMaxEnergyStored() { return energy.getMaxEnergyStored(); }
        @Override public boolean canExtract() { return false; }
        @Override public boolean canReceive() { return true; }
    };

    public PhotosmelterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.PHOTOSMELTER.get(), pos, state);
    }

    public IEnergyStorage getEnergyStorage(@Nullable Direction direction) {
        if (direction == null) return energy;
        if (direction == Direction.UP) return RECEIVE_ONLY;
        return EMPTY_ENERGY;
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) return;

        pullEnergyFromAbove();

        ItemStack in = items.get(SLOT_INPUT);
        SingleRecipeInput input = new SingleRecipeInput(in);
        RecipeHolder<SmeltingRecipe> match = level.getRecipeManager()
                .getRecipeFor(RecipeType.SMELTING, input, level)
                .orElse(null);

        boolean canWork = match != null && canSmelt(match, input);
        if (canWork) {
            if (maxProgress == 0) {
                maxProgress = match.value().getCookingTime();
                energyPaidThisCycle = 0;
            }

            int remainingForItem = Math.max(0, ENERGY_PER_ITEM - energyPaidThisCycle);
            int perTickBudget = Math.min(ENERGY_PER_TICK_LIMIT, remainingForItem);

            if (perTickBudget > 0) {
                int extracted = energy.extractInternal(perTickBudget);
                energyPaidThisCycle += extracted;
                if (extracted > 0) {
                    progress++;
                    setChanged();
                }
            } else {
                progress++;
                setChanged();
            }

            boolean readyByTime = progress >= maxProgress;
            if (readyByTime) {
                int stillOwed = Math.max(0, ENERGY_PER_ITEM - energyPaidThisCycle);
                if (stillOwed > 0) {
                    int paidNow = energy.extractInternal(stillOwed);
                    energyPaidThisCycle += paidNow;
                    if (paidNow < stillOwed) {
                        progress = maxProgress - 1;
                        setChanged();
                        updateLit(progress > 0);
                        return;
                    }
                }

                smelt(match, input);
                progress = 0;
                maxProgress = 0;
                energyPaidThisCycle = 0;
                setChanged();
            }
        } else {
            if (progress != 0 || maxProgress != 0) {
                progress = 0;
                maxProgress = 0;
                energyPaidThisCycle = 0;
                setChanged();
            }
        }

        updateLit(progress > 0);
    }


    private void pullEnergyFromAbove() {
        if (level == null) return;
        ModEnergyUtil.move(
                level,
                worldPosition.above(), Direction.DOWN,
                worldPosition, Direction.UP,
                Math.min(MAX_RECEIVE, ENERGY_PER_TICK_LIMIT * 4)
        );
    }

    private boolean canSmelt(RecipeHolder<SmeltingRecipe> holder, SingleRecipeInput input) {
        if (input.item().isEmpty()) return false;
        ItemStack result = holder.value().assemble(input, level.registryAccess());
        if (result.isEmpty()) return false;
        ItemStack out = items.get(SLOT_OUTPUT);
        if (out.isEmpty()) return true;
        if (!ItemStack.isSameItemSameComponents(out, result)) return false;
        int newCount = out.getCount() + result.getCount();
        return newCount <= out.getMaxStackSize();
    }

    private void smelt(RecipeHolder<SmeltingRecipe> holder, SingleRecipeInput input) {
        ItemStack result = holder.value().assemble(input, level.registryAccess()).copy();
        ItemStack out = items.get(SLOT_OUTPUT);
        if (out.isEmpty()) {
            items.set(SLOT_OUTPUT, result);
        } else if (ItemStack.isSameItemSameComponents(out, result)) {
            out.grow(result.getCount());
        }
        items.get(SLOT_INPUT).shrink(1);
    }

    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        setChanged();
    }

    public int getProgress() {
        return progress;
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    private void updateLit(boolean lit) {
        if (level == null) return;
        BlockState s = getBlockState();
        if (!s.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT)) return;
        if (s.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT) != lit) {
            level.setBlock(worldPosition, s.setValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT, lit), Block.UPDATE_ALL);
        }
    }

    public void drops() {
        if (level == null || level.isClientSide()) return;

        SimpleContainer container = new SimpleContainer(INV_SIZE);
        for (int i = 0; i < INV_SIZE; i++) {
            container.setItem(i, items.get(i).copy());
            items.set(i, ItemStack.EMPTY);
        }
        Containers.dropContents(level, worldPosition, container);
        setChanged();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.solarium.photosmelter");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
        return new PhotosmelterMenu(windowId, inv, this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("Progress", progress);
        tag.putInt("MaxProgress", maxProgress);
        tag.putInt("Energy", energy.getEnergyStored());
        tag.putInt("EnergyPaidThisCycle", energyPaidThisCycle);
        ContainerHelper.saveAllItems(tag, items, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        progress = tag.getInt("Progress");
        maxProgress = tag.getInt("MaxProgress");
        energy.setEnergy(tag.getInt("Energy"));
        energyPaidThisCycle = tag.getInt("EnergyPaidThisCycle");
        items = NonNullList.withSize(INV_SIZE, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, items, registries);
    }
}