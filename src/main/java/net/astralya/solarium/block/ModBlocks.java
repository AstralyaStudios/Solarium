package net.astralya.solarium.block;

import net.astralya.solarium.SolariumMod;
import net.astralya.solarium.block.custom.PhotosmelterBlock;
import net.astralya.solarium.block.custom.SunflowerGeneratorBlock;
import net.astralya.solarium.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SolariumMod.MODID);

    // Energy Generators
    public static final DeferredBlock<Block> SUNFLOWER_GENERATOR = registerBlock("sunflower_generator",
            () -> new SunflowerGeneratorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).noOcclusion()));

    // Decorative Blocks
    public static final DeferredBlock<Block> LED_PANEL = registerBlock("led_panel",
            () -> new Block(BlockBehaviour.Properties.of().noOcclusion().lightLevel(state -> 15)));

    // Machines Blocks
    public static final DeferredBlock<Block> BIOMASS_GENERATOR = registerBlock("biomass_generator",
            () -> new Block(BlockBehaviour.Properties.of().noOcclusion()));
    public static final DeferredBlock<Block> PHOTOSMELTER = registerBlock("photosmelter",
            () -> new PhotosmelterBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));
    public static final DeferredBlock<Block> CABLE = registerBlock("cable",
            () -> new Block(BlockBehaviour.Properties.of().noOcclusion()));

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
