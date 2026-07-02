package net.astralya.solareum.energy;

import net.minecraft.core.Direction;

public final class LuxTransferUtil {
    private LuxTransferUtil() {
    }

    public static int transfer(
            LuxBlockProvider source, Direction sourceSide, LuxBlockProvider target, Direction targetSide, int maxAmount) {
        if (!source.getSideMode(sourceSide).allowsOutput() || !target.getSideMode(targetSide).allowsInput()) {
            return 0;
        }

        return transfer(source.getLuxStorage(sourceSide), target.getLuxStorage(targetSide), maxAmount);
    }

    public static int transfer(LuxStorage source, LuxStorage target, int maxAmount) {
        int available = source.extract(maxAmount, true);
        int accepted = target.receive(available, true);
        if (accepted <= 0) {
            return 0;
        }

        int extracted = source.extract(accepted, false);
        return target.receive(extracted, false);
    }
}
