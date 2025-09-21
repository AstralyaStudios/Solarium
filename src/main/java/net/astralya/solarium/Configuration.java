package net.astralya.solarium;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Configuration {

    public static ModConfigSpec COMMON_CONFIG;
    public static ModConfigSpec CLIENT_CONFIG;

    // ---- Category: Generators -> Sunflower
    public static final String CATEGORY_GENERATORS = "generators";
    public static final String CATEGORY_SUNFLOWER  = "sunflower_generator";

    public static ModConfigSpec.IntValue SUNFLOWER_MAX_PRODUCTION_PER_TICK;
    public static ModConfigSpec.IntValue SUNFLOWER_ENERGY_TRANSFER_AMOUNT;
    public static ModConfigSpec.IntValue SUNFLOWER_CAPACITY;
    public static ModConfigSpec.BooleanValue SUNFLOWER_EMIT_PARTICLES;

    static {
        ModConfigSpec.Builder COMMON_BUILDER = new ModConfigSpec.Builder();

        // ===== GENERATORS =====
        COMMON_BUILDER.comment("Generators settings").push(CATEGORY_GENERATORS);

        // --- Sunflower Generator (basic solar)
        COMMON_BUILDER.comment("Sunflower Generator (basic solar) settings").push(CATEGORY_SUNFLOWER);

        SUNFLOWER_MAX_PRODUCTION_PER_TICK = COMMON_BUILDER
                .comment("""
                        Peak FE/t the Sunflower Generator can produce at noon under clear sky.
                        Default: 5
                        """)
                .defineInRange("maxProductionPerTick", 5, 0, 200);

        SUNFLOWER_ENERGY_TRANSFER_AMOUNT = COMMON_BUILDER
                .comment("""
                        Max FE/t this block will push to neighbors (throughput).
                        Default: 50
                        """)
                .defineInRange("energyTransferPerTick", 50, 0, 5000);

        SUNFLOWER_CAPACITY = COMMON_BUILDER
                .comment("""
                        Internal energy buffer (FE).
                        Default: 5000
                        """)
                .defineInRange("capacity", 5000, 0, 10_000_000);

        SUNFLOWER_EMIT_PARTICLES = COMMON_BUILDER
                .comment("""
                        Whether the Sunflower Generator should emit production particles when generating energy.
                        Default: true
                        """)
                .define("emitParticles", true);

        COMMON_BUILDER.pop();
        COMMON_BUILDER.pop();

        COMMON_CONFIG = COMMON_BUILDER.build();

        ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();
        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }
}
