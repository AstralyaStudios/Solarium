package net.astralya.solareum.item.custom;

import net.astralya.solareum.block.entity.custom.LeafPanelBlockEntity;
import net.astralya.solareum.block.entity.custom.MossCapacitorBlockEntity;
import net.astralya.solareum.block.entity.custom.SeedPressBlockEntity;
import net.astralya.solareum.block.entity.custom.VineConduitBlockEntity;
import net.astralya.solareum.energy.LuxBlockProvider;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class LuxMeterItem extends Item {
    public LuxMeterItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getLevel().isClientSide) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity blockEntity = context.getLevel().getBlockEntity(context.getClickedPos());
        context.getPlayer().displayClientMessage(getDiagnosticMessage(blockEntity, context.getClickedFace()), false);
        return InteractionResult.CONSUME;
    }

    private static Component getDiagnosticMessage(BlockEntity blockEntity, Direction clickedSide) {
        String sideMode = getSideModeText(blockEntity, clickedSide);
        if (blockEntity instanceof LeafPanelBlockEntity leafPanel) {
            return Component.literal(
                    "Leaf Panel: "
                            + leafPanel.getStoredLux()
                            + " / "
                            + leafPanel.getCapacity()
                            + " Lux, generating: "
                            + (leafPanel.canGenerate() ? "yes" : "no")
                            + sideMode);
        }

        if (blockEntity instanceof MossCapacitorBlockEntity mossCapacitor) {
            return Component.literal(
                    "Moss Capacitor: "
                            + mossCapacitor.getStoredLux()
                            + " / "
                            + mossCapacitor.getCapacity()
                            + " Lux"
                            + sideMode);
        }

        if (blockEntity instanceof VineConduitBlockEntity vineConduit) {
            return Component.literal(
                    "Vine Conduit: "
                            + vineConduit.getStoredLux()
                            + " / "
                            + vineConduit.getCapacity()
                            + " Lux"
                            + sideMode);
        }

        if (blockEntity instanceof SeedPressBlockEntity seedPress) {
            return Component.literal(
                    "Seed Press: "
                            + seedPress.getStoredLux()
                            + " / "
                            + seedPress.getCapacity()
                            + " Lux"
                            + sideMode);
        }

        return Component.literal("No Lux storage detected");
    }

    private static String getSideModeText(BlockEntity blockEntity, Direction clickedSide) {
        if (blockEntity instanceof LuxBlockProvider provider) {
            return ", " + clickedSide.getSerializedName() + ": " + provider.getSideMode(clickedSide).name();
        }

        return "";
    }
}
