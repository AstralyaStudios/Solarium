package net.astralya.solarium.screen.custom;

import net.astralya.solarium.block.entity.custom.PhotosmelterBlockEntity;
import net.astralya.solarium.screen.ModMenuTypes;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class PhotosmelterMenu extends AbstractContainerMenu {
    public final @Nullable PhotosmelterBlockEntity blockEntity;
    private final ContainerLevelAccess access;
    private final Container adapter;

    private int cProgress;
    private int cMaxProgress;
    private int cEnergy;
    private int cCapacity;

    public PhotosmelterMenu(int id, Inventory inv) {
        super(ModMenuTypes.PHOTOSMELTER.get(), id);
        this.blockEntity = null;
        this.access = ContainerLevelAccess.NULL;

        this.adapter = new SimpleContainer(2);

        this.addSlot(new Slot(adapter, 0, 56, 35));
        this.addSlot(new Slot(adapter, 1, 116, 35) { @Override public boolean mayPlace(ItemStack stack) { return false; } });

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                this.addSlot(new Slot(inv, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
            }
        }
        for (int x = 0; x < 9; x++) {
            this.addSlot(new Slot(inv, x, 8 + x * 18, 142));
        }

        bindDataSlots(null);
    }

    public PhotosmelterMenu(int id, Inventory inv, PhotosmelterBlockEntity be) {
        super(ModMenuTypes.PHOTOSMELTER.get(), id);
        this.blockEntity = be;
        this.access = ContainerLevelAccess.create(inv.player.level(), be.getBlockPos());

        this.adapter = new Container() {
            @Override public int getContainerSize() { return 2; }
            @Override public boolean isEmpty() { return be.getItem(0).isEmpty() && be.getItem(1).isEmpty(); }
            @Override public ItemStack getItem(int slot) { return be.getItem(slot); }
            @Override public ItemStack removeItem(int slot, int amount) {
                ItemStack stack = be.getItem(slot);
                if (stack.isEmpty()) return ItemStack.EMPTY;
                ItemStack split = stack.split(amount);
                be.setItem(slot, stack.isEmpty() ? ItemStack.EMPTY : stack);
                return split;
            }
            @Override public ItemStack removeItemNoUpdate(int slot) {
                ItemStack stack = be.getItem(slot);
                be.setItem(slot, ItemStack.EMPTY);
                return stack;
            }
            @Override public void setItem(int slot, ItemStack stack) { be.setItem(slot, stack); }
            @Override public void setChanged() { be.setChanged(); }
            @Override public boolean stillValid(Player player) { return true; }
            @Override public void clearContent() { be.setItem(0, ItemStack.EMPTY); be.setItem(1, ItemStack.EMPTY); }
        };

        this.addSlot(new Slot(adapter, 0, 56, 35));
        this.addSlot(new Slot(adapter, 1, 116, 35) { @Override public boolean mayPlace(ItemStack stack) { return false; } });

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                this.addSlot(new Slot(inv, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
            }
        }
        for (int x = 0; x < 9; x++) {
            this.addSlot(new Slot(inv, x, 8 + x * 18, 142));
        }

        bindDataSlots(be);
    }

    private void bindDataSlots(@Nullable PhotosmelterBlockEntity be) {
        this.addDataSlot(new DataSlot() {
            @Override public int get() { return be != null ? be.getProgress() : 0; }
            @Override public void set(int value) { cProgress = value; }
        });
        this.addDataSlot(new DataSlot() {
            @Override public int get() { return be != null ? be.getMaxProgress() : 0; }
            @Override public void set(int value) { cMaxProgress = value; }
        });
        this.addDataSlot(new DataSlot() {
            @Override public int get() { return be != null ? be.getEnergyStorage(null).getEnergyStored() : 0; }
            @Override public void set(int value) { cEnergy = value; }
        });
        this.addDataSlot(new DataSlot() {
            @Override public int get() { return be != null ? be.getEnergyStorage(null).getMaxEnergyStored() : 0; }
            @Override public void set(int value) { cCapacity = value; }
        });
    }

    @Override
    public boolean stillValid(Player player) {
        if (blockEntity == null) return true;
        return stillValid(access, player, blockEntity.getBlockState().getBlock());
    }

    public int getProgress() {
        return isClient() ? cProgress : (blockEntity != null ? blockEntity.getProgress() : 0);
    }

    public int getMaxProgress() {
        return isClient() ? cMaxProgress : (blockEntity != null ? blockEntity.getMaxProgress() : 0);
    }

    public int getEnergy() {
        return isClient() ? cEnergy : (blockEntity != null ? blockEntity.getEnergyStorage(null).getEnergyStored() : 0);
    }

    public int getCapacity() {
        return isClient() ? cCapacity : (blockEntity != null ? blockEntity.getEnergyStorage(null).getMaxEnergyStored() : 0);
    }

    public boolean isCrafting() {
        return getProgress() > 0 && getMaxProgress() > 0;
    }

    public int getScaledArrowProgress() {
        int cur = getProgress();
        int max = getMaxProgress();
        int arrowPixelSize = 24;
        return (max > 0 && cur > 0) ? (cur * arrowPixelSize) / max : 0;
    }

    private boolean isClient() {
        return blockEntity == null || (blockEntity.getLevel() != null && blockEntity.getLevel().isClientSide());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack ret = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            ret = stack.copy();
            if (index < 2) {
                if (!this.moveItemStackTo(stack, 2, 38, true)) return ItemStack.EMPTY;
            } else {
                if (!this.moveItemStackTo(stack, 0, 1, false)) {
                    if (index < 29) {
                        if (!this.moveItemStackTo(stack, 29, 38, false)) return ItemStack.EMPTY;
                    } else if (!this.moveItemStackTo(stack, 2, 29, false)) return ItemStack.EMPTY;
                }
            }
            if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
            else slot.setChanged();
        }
        return ret;
    }
}