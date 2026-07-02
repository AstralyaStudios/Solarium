package net.astralya.solareum.block.custom;

import com.mojang.serialization.MapCodec;
import net.astralya.solareum.block.entity.custom.MossCapacitorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public final class MossCapacitorBlock extends BaseEntityBlock {
    public static final MapCodec<MossCapacitorBlock> CODEC = simpleCodec(MossCapacitorBlock::new);

    public MossCapacitorBlock(Properties properties) {
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
        return new MossCapacitorBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof MossCapacitorBlockEntity mossCapacitor) {
            player.displayClientMessage(
                    Component.literal(
                            mossCapacitor.getStoredLux() + " / " + mossCapacitor.getCapacity() + " Lux"),
                    true);
            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }
}
