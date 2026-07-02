package net.astralya.solareum.block.custom;

import com.mojang.serialization.MapCodec;
import net.astralya.solareum.block.entity.custom.SeedPressBlockEntity;
import net.astralya.solareum.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public final class SeedPressBlock extends BaseEntityBlock {
    private static final int PROCESS_LUX_COST = 100;

    public static final MapCodec<SeedPressBlock> CODEC = simpleCodec(SeedPressBlock::new);

    public SeedPressBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SeedPressBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof SeedPressBlockEntity seedPress) {
            player.displayClientMessage(
                    Component.literal(seedPress.getStoredLux() + " / " + seedPress.getCapacity() + " Lux"),
                    true);
            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }

    @Override
    protected ItemInteractionResult useItemOn(
            ItemStack heldStack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hitResult) {
        if (!heldStack.is(Items.WHEAT_SEEDS)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof SeedPressBlockEntity seedPress)
                || seedPress.getStoredLux() < PROCESS_LUX_COST) {
            return ItemInteractionResult.CONSUME;
        }

        seedPress.getLuxStorage().extract(PROCESS_LUX_COST, false);
        if (!player.getAbilities().instabuild) {
            heldStack.shrink(1);
        }

        ItemStack output = new ItemStack(ModItems.BIO_FIBER.get(), 2);
        if (!player.addItem(output)) {
            player.drop(output, false);
        }

        seedPress.setChanged();
        level.playSound(null, pos, SoundEvents.WOOD_HIT, SoundSource.BLOCKS, 0.7F, 1.1F);
        return ItemInteractionResult.CONSUME;
    }
}
