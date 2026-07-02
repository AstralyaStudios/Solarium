package net.astralya.solareum.energy;

import net.minecraft.core.Direction;

public interface LuxBlockProvider {
    LuxStorage getLuxStorage(Direction side);

    LuxSideMode getSideMode(Direction side);

    void setSideMode(Direction side, LuxSideMode mode);
}
