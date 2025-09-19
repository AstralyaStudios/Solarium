package net.astralya.solarium.item.custom;

import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.List;

public class EnergyMeterItem extends Item {

    public EnergyMeterItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Level level = ctx.getLevel();
        Player player = ctx.getPlayer();
        if (player == null) return InteractionResult.PASS;
        if (!player.isCrouching()) return InteractionResult.PASS;

        BlockPos pos = ctx.getClickedPos();

        if (!level.isClientSide()) {
            IEnergyStorage energy = level.getCapability(
                    Capabilities.EnergyStorage.BLOCK, pos, ctx.getClickedFace());
            if (energy == null) {
                energy = level.getCapability(Capabilities.EnergyStorage.BLOCK, pos, null);
            }

            if (energy != null) {
                int stored = energy.getEnergyStored();
                int cap = energy.getMaxEnergyStored();
                player.displayClientMessage(
                        Component.translatable("message.solarium.energy_meter.reading", stored, cap),
                        true
                );
                return InteractionResult.sidedSuccess(true);
            } else {
                player.displayClientMessage(
                        Component.translatable("message.solarium.energy_meter.none"),
                        true
                );
                return InteractionResult.sidedSuccess(true);
            }
        }

        return InteractionResult.sidedSuccess(true);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.solarium.energy_item").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.solarium.energy_item.crouch").withStyle(ChatFormatting.DARK_GRAY));
    }
}