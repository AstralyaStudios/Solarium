package net.astralya.solareum;

import net.astralya.solareum.block.ModBlocks;
import net.astralya.solareum.block.entity.ModBlockEntityTypes;
import net.astralya.solareum.item.ModCreativeModeTabs;
import net.astralya.solareum.item.ModItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Solareum {
    public static final String MOD_ID = "solareum";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private Solareum() {
    }

    public static void init() {
        ModBlocks.init();
        ModItems.init();
        ModBlockEntityTypes.init();
        ModCreativeModeTabs.init();
    }

    public static void initClient() {
    }
}
