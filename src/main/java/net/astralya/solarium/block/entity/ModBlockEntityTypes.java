package net.astralya.solarium.block.entity;

import net.astralya.solarium.SolariumMod;
import net.astralya.solarium.block.ModBlocks;
import net.astralya.solarium.block.entity.custom.PhotosmelterBlockEntity;
import net.astralya.solarium.block.entity.custom.SunflowerGeneratorBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntityTypes {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPE =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, SolariumMod.MODID);

    public static final Supplier<BlockEntityType<SunflowerGeneratorBlockEntity>> SUNFLOWER_GENERATOR = BLOCK_ENTITY_TYPE.register("sunflower_generator",
            () -> BlockEntityType.Builder.of(SunflowerGeneratorBlockEntity::new, ModBlocks.SUNFLOWER_GENERATOR.get()).build(null));

    public static final Supplier<BlockEntityType<PhotosmelterBlockEntity>> PHOTOSMELTER = BLOCK_ENTITY_TYPE.register("photosmelter",
            () -> BlockEntityType.Builder.of(PhotosmelterBlockEntity::new, ModBlocks.PHOTOSMELTER.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITY_TYPE.register(eventBus);
    }
}
