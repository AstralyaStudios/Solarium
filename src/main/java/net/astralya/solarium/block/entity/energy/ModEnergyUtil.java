package net.astralya.solarium.block.entity.energy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class ModEnergyUtil {

    public static int move(Level level, BlockPos fromPos, Direction fromSide, BlockPos toPos, Direction toSide, int maxAmount) {

        if (maxAmount <= 0 || level == null) return 0;

        IEnergyStorage from = level.getCapability(Capabilities.EnergyStorage.BLOCK, fromPos, fromSide);
        IEnergyStorage to   = level.getCapability(Capabilities.EnergyStorage.BLOCK, toPos,   toSide);

        if (from == null || to == null) return 0;
        if (!from.canExtract() || !to.canReceive()) return 0;

        int canExtract = from.extractEnergy(maxAmount, true);
        if (canExtract <= 0) return 0;

        int canReceive = to.receiveEnergy(canExtract, true);
        if (canReceive <= 0) return 0;

        int toMove = Math.min(canExtract, canReceive);

        int extracted = from.extractEnergy(toMove, false);
        if (extracted <= 0) return 0;

        int accepted = to.receiveEnergy(extracted, false);
        if (accepted < extracted) {
        }
        return accepted;
    }

    public static boolean move(Level level, BlockPos fromPos, Direction fromSide, BlockPos toPos, Direction toSide, int maxAmount, boolean requirePositive) {
        int moved = move(level, fromPos, fromSide, toPos, toSide, maxAmount);
        return !requirePositive || moved > 0;
    }

    public static boolean hasEnergyAt(Level level, BlockPos pos, Direction side) {
        return level != null && level.getCapability(Capabilities.EnergyStorage.BLOCK, pos, side) != null;
    }
}
