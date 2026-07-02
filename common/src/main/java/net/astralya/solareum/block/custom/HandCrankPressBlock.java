package net.astralya.solareum.block.custom;

import net.astralya.solareum.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public final class HandCrankPressBlock extends Block {
    public HandCrankPressBlock(Properties properties) {
        super(properties);
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
        int bioFiberCount = getBioFiberCount(heldStack);
        if (bioFiberCount == 0) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }

        if (!player.getAbilities().instabuild) {
            heldStack.shrink(1);
        }

        ItemStack output = new ItemStack(ModItems.BIO_FIBER.get(), bioFiberCount);
        if (!player.addItem(output)) {
            player.drop(output, false);
        }

        level.playSound(null, pos, SoundEvents.WOOD_HIT, SoundSource.BLOCKS, 0.7F, 0.9F);
        return ItemInteractionResult.CONSUME;
    }

    private static int getBioFiberCount(ItemStack stack) {
        if (stack.is(Items.VINE)) {
            return 2;
        }

        if (stack.is(ItemTags.LEAVES) || stack.is(Items.BAMBOO)) {
            return 1;
        }

        return 0;
    }
}
