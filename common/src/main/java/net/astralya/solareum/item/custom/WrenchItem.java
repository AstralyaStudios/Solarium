package net.astralya.solareum.item.custom;

import net.astralya.solareum.energy.LuxBlockProvider;
import net.astralya.solareum.energy.LuxSideMode;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class WrenchItem extends Item {
    public WrenchItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getLevel().isClientSide) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity blockEntity = context.getLevel().getBlockEntity(context.getClickedPos());
        if (!(blockEntity instanceof LuxBlockProvider luxBlockProvider)) {
            context.getPlayer().displayClientMessage(Component.literal("No configurable Lux sides detected."), false);
            return InteractionResult.CONSUME;
        }

        Direction clickedSide = context.getClickedFace();
        LuxSideMode newMode = luxBlockProvider.getSideMode(clickedSide).next();
        luxBlockProvider.setSideMode(clickedSide, newMode);
        blockEntity.setChanged();

        context.getPlayer()
                .displayClientMessage(
                        Component.literal(
                                blockEntity.getBlockState().getBlock().getName().getString()
                                        + " "
                                        + clickedSide.getSerializedName()
                                        + ": "
                                        + newMode.name()),
                        false);
        return InteractionResult.CONSUME;
    }
}
