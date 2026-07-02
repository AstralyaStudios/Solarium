package net.astralya.solareum.block;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.astralya.solareum.Solareum;
import net.astralya.solareum.block.custom.HandCrankPressBlock;
import net.astralya.solareum.block.custom.LeafPanelBlock;
import net.astralya.solareum.block.custom.MossCapacitorBlock;
import net.astralya.solareum.block.custom.SeedPressBlock;
import net.astralya.solareum.block.custom.VineConduitBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(Solareum.MOD_ID, Registries.BLOCK);

    public static final RegistrySupplier<Block> HAND_CRANK_PRESS =
            BLOCKS.register(
                    "hand_crank_press",
                    () ->
                            new HandCrankPressBlock(
                                    BlockBehaviour.Properties.of()
                                            .mapColor(MapColor.WOOD)
                                            .strength(1.5F)
                                            .sound(SoundType.WOOD)));

    public static final RegistrySupplier<Block> LEAF_PANEL =
            BLOCKS.register(
                    "leaf_panel",
                    () ->
                            new LeafPanelBlock(
                                    BlockBehaviour.Properties.of()
                                            .mapColor(MapColor.PLANT)
                                            .strength(0.8F)
                                            .sound(SoundType.GRASS)));

    public static final RegistrySupplier<Block> MOSS_CAPACITOR =
            BLOCKS.register(
                    "moss_capacitor",
                    () ->
                            new MossCapacitorBlock(
                                    BlockBehaviour.Properties.of()
                                            .mapColor(MapColor.PLANT)
                                            .strength(1.0F)
                                            .sound(SoundType.MOSS)));

    public static final RegistrySupplier<Block> VINE_CONDUIT =
            BLOCKS.register(
                    "vine_conduit",
                    () ->
                            new VineConduitBlock(
                                    BlockBehaviour.Properties.of()
                                            .mapColor(MapColor.PLANT)
                                            .strength(0.6F)
                                            .sound(SoundType.VINE)));

    public static final RegistrySupplier<Block> SEED_PRESS =
            BLOCKS.register(
                    "seed_press",
                    () ->
                            new SeedPressBlock(
                                    BlockBehaviour.Properties.of()
                                            .mapColor(MapColor.WOOD)
                                            .strength(1.5F)
                                            .sound(SoundType.WOOD)));

    private ModBlocks() {
    }

    public static void init() {
        BLOCKS.register();
    }
}
