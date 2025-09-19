package net.astralya.solarium.block.entity.custom;

import net.astralya.solarium.block.entity.ModBlockEntityTypes;
import net.astralya.solarium.block.entity.custom.energy.ModEnergyStorage;
import net.astralya.solarium.block.entity.custom.energy.ModEnergyUtil;
import net.astralya.solarium.screen.custom.SunflowerGeneratorMenu;
import net.astralya.solarium.util.SunlightCheck;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class SunflowerGeneratorBlockEntity extends SyncBlockEntity implements MenuProvider {

    private static final int MAX_PRODUCTION_PER_TICK = 320;
    private static final int ENERGY_TRANSFER_AMOUNT = 320;

    public final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    private final ModEnergyStorage ENERGY_STORAGE = createEnergyStorage();
    private SunlightCheck sun;
    private int lastProduction;

    protected final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> lastProduction;
                case 1 -> MAX_PRODUCTION_PER_TICK;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) lastProduction = value;
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    public SunflowerGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.SUNFLOWER_GENERATOR.get(), pos, state);
    }

    private ModEnergyStorage createEnergyStorage() {
        return new ModEnergyStorage(64_000, 320) {
            @Override
            public void onEnergyChanged() {
                setChanged();
                getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        };
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level != null && !level.isClientSide()) {
            sun = new SunlightCheck(level, worldPosition);
            lastProduction = 0;
        }
    }

    public IEnergyStorage getEnergyStorage(@Nullable Direction direction) {
        return ENERGY_STORAGE;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.solarium.sunflower_generator");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory inv, Player player) {
        return new SunflowerGeneratorMenu(containerId, inv, this, this.data);
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) return;

        if (sun == null) {
            sun = new SunlightCheck(level, pos);
        }

        sun.recheckCanSeeSun();

        int produced = 0;
        if (ENERGY_STORAGE.getEnergyStored() < ENERGY_STORAGE.getMaxEnergyStored()) {
            float brightness = SunlightCheck.getSunBrightness(level, 1.0F);
            float mult = sun.getGenerationMultiplier();
            float scaled = MAX_PRODUCTION_PER_TICK * (brightness * mult);
            if (scaled > 0) {
                produced = (int) Math.min(MAX_PRODUCTION_PER_TICK, Math.floor(scaled));
                ENERGY_STORAGE.receiveEnergy(produced, false);
            }
        }

        lastProduction = produced;
        pushEnergyToNeighbourAbove();
    }

    private void pushEnergyToNeighbourAbove() {
        if (ModEnergyUtil.doesBlockHaveEnergyStorage(this.worldPosition.above(), this.level)) {
            ModEnergyUtil.move(this.worldPosition, this.worldPosition.above(), ENERGY_TRANSFER_AMOUNT, this.level);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put("sunflower_generator.inventory", itemHandler.serializeNBT(registries));
        tag.putInt("sunflower_generator.energy", ENERGY_STORAGE.getEnergyStored());
        tag.putInt("sunflower_generator.last_production", lastProduction);
        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        itemHandler.deserializeNBT(registries, tag.getCompound("sunflower_generator.inventory"));
        ENERGY_STORAGE.setEnergy(tag.getInt("sunflower_generator.energy"));
        lastProduction = tag.getInt("sunflower_generator.last_production");
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
        super.onDataPacket(net, pkt, lookupProvider);
    }
}