package net.astralya.solareum.neoforge;

import net.astralya.solareum.Solareum;
import net.astralya.solareum.neoforge.energy.NeoForgeLuxCapabilities;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(Solareum.MOD_ID)
public final class SolareumNeoForge {
    public SolareumNeoForge(IEventBus modEventBus, ModContainer modContainer) {
        Solareum.init();
        modEventBus.addListener(NeoForgeLuxCapabilities::register);
    }
}
