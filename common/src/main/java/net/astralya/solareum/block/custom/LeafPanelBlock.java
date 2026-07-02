package net.astralya.solareum.block.custom;

import com.mojang.serialization.MapCodec;
import net.astralya.solareum.block.entity.ModBlockEntityTypes;
import net.astralya.solareum.block.entity.custom.LeafPanelBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public final class LeafPanelBlock extends BaseEntityBlock {
    public static final MapCodec<LeafPanelBlock> CODEC = simpleCodec(LeafPanelBlock::new);

    public LeafPanelBlock(Properties properties) {
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
        return new LeafPanelBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) {
            return null;
        }

        return createTickerHelper(
                type, ModBlockEntityTypes.LEAF_PANEL.get(), LeafPanelBlockEntity::serverTick);
    }
}
