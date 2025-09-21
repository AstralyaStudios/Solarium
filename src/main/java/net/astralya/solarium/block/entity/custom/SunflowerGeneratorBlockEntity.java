package net.astralya.solarium.block.entity.custom;

import net.astralya.solarium.Configuration;
import net.astralya.solarium.block.custom.SunflowerGeneratorBlock;
import net.astralya.solarium.block.entity.ModBlockEntityTypes;
import net.astralya.solarium.block.entity.energy.ModEnergyStorage;
import net.astralya.solarium.block.entity.energy.ModEnergyUtil;
import net.astralya.solarium.screen.custom.SunflowerGeneratorMenu;
import net.astralya.solarium.util.SunlightCheck;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class SunflowerGeneratorBlockEntity extends SyncBlockEntity implements MenuProvider {

    private final ModEnergyStorage ENERGY_STORAGE = createEnergyStorage();
    private SunlightCheck sun;
    private int lastProduction;
    private int particleCooldown = 0;

    private static final float[][] PANEL_CENTERS = new float[][]{
            {4f, 1.75f, 8f},
            {12f, 1.75f, 8f},
            {8f, 1.75f, 4f},
            {8f, 1.75f, 12f}
    };

    public final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    protected final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> lastProduction;
                case 1 -> maxProductionPerTick();
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
        return new ModEnergyStorage(capacity(), energyTransferPerTick()) {
            @Override
            public void onEnergyChanged() {
                setChanged();
                Level level = getLevel();
                if (level != null && !level.isClientSide()) {
                    level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
                }
            }
        };
    }

    private int maxProductionPerTick() {
        return Configuration.SUNFLOWER_MAX_PRODUCTION_PER_TICK.get();
    }

    private int energyTransferPerTick() {
        return Configuration.SUNFLOWER_ENERGY_TRANSFER_AMOUNT.get();
    }

    private int capacity() {
        return Configuration.SUNFLOWER_CAPACITY.get();
    }

    private boolean emitParticles() {
        return Configuration.SUNFLOWER_EMIT_PARTICLES.get();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level != null && !level.isClientSide()) {
            sun = new SunlightCheck(level, worldPosition);
            lastProduction = 0;
            sun.recheckCanSeeSun();
            updateLit(sun.canSeeSunNow());
        }
    }

    private static final IEnergyStorage EMPTY_ENERGY = new IEnergyStorage() {
        @Override public int receiveEnergy(int maxReceive, boolean simulate) { return 0; }
        @Override public int extractEnergy(int maxExtract, boolean simulate) { return 0; }
        @Override public int getEnergyStored() { return 0; }
        @Override public int getMaxEnergyStored() { return 0; }
        @Override public boolean canExtract() { return false; }
        @Override public boolean canReceive() { return false; }
    };

    private final IEnergyStorage EXTRACT_ONLY = new IEnergyStorage() {
        @Override public int receiveEnergy(int maxReceive, boolean simulate) { return 0; }
        @Override public int extractEnergy(int maxExtract, boolean simulate) { return ENERGY_STORAGE.extractEnergy(maxExtract, simulate); }
        @Override public int getEnergyStored() { return ENERGY_STORAGE.getEnergyStored(); }
        @Override public int getMaxEnergyStored() { return ENERGY_STORAGE.getMaxEnergyStored(); }
        @Override public boolean canExtract() { return true; }
        @Override public boolean canReceive() { return false; }
    };

    public IEnergyStorage getEnergyStorage(@Nullable Direction direction) {
        if (direction == null) return ENERGY_STORAGE;
        if (direction == Direction.DOWN) return EXTRACT_ONLY;
        return EMPTY_ENERGY;
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
            float scaled = maxProductionPerTick() * (brightness * mult);
            if (scaled > 0) {
                produced = Math.min(maxProductionPerTick(), (int) Math.floor(scaled));
                if (produced > 0) {
                    ENERGY_STORAGE.receiveEnergy(produced, false);
                }
            }
        }

        lastProduction = produced;
        updateLit(sun.canSeeSunNow());
        spawnPanelParticles(level, pos, state, produced);
        pushEnergyToNeighbourBelow();
    }

    private void updateLit(boolean lit) {
        if (level == null) return;
        BlockState state = getBlockState();
        if (!state.hasProperty(SunflowerGeneratorBlock.LIT)) return;
        boolean currentlyLit = state.getValue(SunflowerGeneratorBlock.LIT);
        if (currentlyLit != lit) {
            level.setBlock(worldPosition, state.setValue(SunflowerGeneratorBlock.LIT, lit), Block.UPDATE_ALL);
        }
    }

    private void spawnPanelParticles(Level level, BlockPos pos, BlockState state, int produced) {
        if (!emitParticles()) return;
        if (!(level instanceof ServerLevel server)) return;
        if (produced <= 0) return;

        if (particleCooldown > 0) {
            particleCooldown--;
            return;
        }
        particleCooldown = 8;

        int idx = server.getRandom().nextInt(PANEL_CENTERS.length);
        float cx = PANEL_CENTERS[idx][0];
        float cy = PANEL_CENTERS[idx][1];
        float cz = PANEL_CENTERS[idx][2];

        double dx = cx - 8.0;
        double dz = cz - 8.0;

        Direction facing = state.getValue(SunflowerGeneratorBlock.FACING);
        double[] r = rotateByFacing(dx, dz, facing);
        double rx = r[0];
        double rz = r[1];

        double x = pos.getX() + 0.5 + rx / 16.0 + (server.getRandom().nextDouble() - 0.5) * 0.02;
        double y = pos.getY() + (cy / 16.0) + 0.02 + server.getRandom().nextDouble() * 0.02;
        double z = pos.getZ() + 0.5 + rz / 16.0 + (server.getRandom().nextDouble() - 0.5) * 0.02;

        Vector3f from = new Vector3f(255 / 255f, 217 / 255f, 59 / 255f);
        Vector3f to = new Vector3f(107 / 255f, 142 / 255f, 35 / 255f);
        DustColorTransitionOptions dust = new DustColorTransitionOptions(from, to, 0.7F);

        server.sendParticles(dust, x, y, z, 1, 0.0, 0.0, 0.0, 0.0);
    }

    private static double[] rotateByFacing(double dx, double dz, Direction facing) {
        return switch (facing) {
            case NORTH -> new double[]{dx, dz};
            case EAST -> new double[]{dz, -dx};
            case SOUTH -> new double[]{-dx, -dz};
            case WEST -> new double[]{-dz, dx};
            default -> new double[]{dx, dz};
        };
    }

    private void pushEnergyToNeighbourBelow() {
        if (level == null) return;
        ModEnergyUtil.move(
                level,
                this.worldPosition, Direction.DOWN,
                this.worldPosition.below(), Direction.UP,
                energyTransferPerTick()
        );
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put("sunflower_generator.inventory", itemHandler.serializeNBT(registries));
        tag.putInt("sunflower_generator.energy", ENERGY_STORAGE.getEnergyStored());
        tag.putInt("sunflower_generator.last_production", lastProduction);
        tag.putInt("sunflower_generator.particle_cooldown", particleCooldown);
        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        itemHandler.deserializeNBT(registries, tag.getCompound("sunflower_generator.inventory"));
        ENERGY_STORAGE.setEnergy(tag.getInt("sunflower_generator.energy"));
        lastProduction = tag.getInt("sunflower_generator.last_production");
        particleCooldown = tag.getInt("sunflower_generator.particle_cooldown");
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