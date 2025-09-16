package net.astralya.solarium;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Configuration {

    public static ModConfigSpec COMMON_CONFIG;
    public static ModConfigSpec CLIENT_CONFIG;

    static {
        ModConfigSpec.Builder COMMON_BUILDER = new ModConfigSpec.Builder();

        ///

        COMMON_CONFIG = COMMON_BUILDER.build();
        ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();
        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }
}
