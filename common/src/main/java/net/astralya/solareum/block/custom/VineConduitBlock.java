package net.astralya.solareum.block.custom;

import com.mojang.serialization.MapCodec;
import net.astralya.solareum.block.entity.ModBlockEntityTypes;
import net.astralya.solareum.block.entity.custom.VineConduitBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public final class VineConduitBlock extends BaseEntityBlock {
    public static final MapCodec<VineConduitBlock> CODEC = simpleCodec(VineConduitBlock::new);

    public VineConduitBlock(Properties properties) {
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
        return new VineConduitBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) {
            return null;
        }

        return createTickerHelper(
                type, ModBlockEntityTypes.VINE_CONDUIT.get(), VineConduitBlockEntity::serverTick);
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof VineConduitBlockEntity vineConduit) {
            player.displayClientMessage(
                    Component.literal(
                            vineConduit.getStoredLux() + " / " + vineConduit.getCapacity() + " Lux"),
                    true);
            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }
}
