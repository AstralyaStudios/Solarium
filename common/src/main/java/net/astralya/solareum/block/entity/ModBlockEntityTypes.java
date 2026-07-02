package net.astralya.solareum.block.entity;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.astralya.solareum.Solareum;
import net.astralya.solareum.block.ModBlocks;
import net.astralya.solareum.block.entity.custom.LeafPanelBlockEntity;
import net.astralya.solareum.block.entity.custom.MossCapacitorBlockEntity;
import net.astralya.solareum.block.entity.custom.SeedPressBlockEntity;
import net.astralya.solareum.block.entity.custom.VineConduitBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class ModBlockEntityTypes {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Solareum.MOD_ID, Registries.BLOCK_ENTITY_TYPE);

    public static final RegistrySupplier<BlockEntityType<LeafPanelBlockEntity>> LEAF_PANEL =
            BLOCK_ENTITY_TYPES.register(
                    "leaf_panel",
                    () -> BlockEntityType.Builder.of(LeafPanelBlockEntity::new, ModBlocks.LEAF_PANEL.get())
                            .build(null));

    public static final RegistrySupplier<BlockEntityType<MossCapacitorBlockEntity>> MOSS_CAPACITOR =
            BLOCK_ENTITY_TYPES.register(
                    "moss_capacitor",
                    () -> BlockEntityType.Builder.of(
                                    MossCapacitorBlockEntity::new, ModBlocks.MOSS_CAPACITOR.get())
                            .build(null));

    public static final RegistrySupplier<BlockEntityType<VineConduitBlockEntity>> VINE_CONDUIT =
            BLOCK_ENTITY_TYPES.register(
                    "vine_conduit",
                    () -> BlockEntityType.Builder.of(
                                    VineConduitBlockEntity::new, ModBlocks.VINE_CONDUIT.get())
                            .build(null));

    public static final RegistrySupplier<BlockEntityType<SeedPressBlockEntity>> SEED_PRESS =
            BLOCK_ENTITY_TYPES.register(
                    "seed_press",
                    () -> BlockEntityType.Builder.of(SeedPressBlockEntity::new, ModBlocks.SEED_PRESS.get())
                            .build(null));

    private ModBlockEntityTypes() {
    }

    public static void init() {
        BLOCK_ENTITY_TYPES.register();
    }
}
