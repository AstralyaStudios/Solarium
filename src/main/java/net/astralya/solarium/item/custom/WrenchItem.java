package net.astralya.solarium.item.custom;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class WrenchItem extends Item {

    public WrenchItem(Properties properties) {
        super(properties);
    }

    public static boolean isWrench (ItemStack stack) {
        return stack.getItem() instanceof WrenchItem;
    }

    public static boolean isHoldingWrench (Player player) {
        return isWrench(player.getMainHandItem()) || isWrench(player.getOffhandItem());
    }
}
