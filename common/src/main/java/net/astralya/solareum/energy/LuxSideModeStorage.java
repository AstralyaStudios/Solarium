package net.astralya.solareum.energy;

import java.util.Arrays;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

public final class LuxSideModeStorage {
    private static final String SIDE_MODES_KEY = "LuxSideModes";

    private LuxSideModeStorage() {
    }

    public static LuxSideMode[] create(LuxSideMode defaultMode) {
        LuxSideMode[] sideModes = new LuxSideMode[Direction.values().length];
        Arrays.fill(sideModes, defaultMode);
        return sideModes;
    }

    public static LuxSideMode get(LuxSideMode[] sideModes, Direction side) {
        return sideModes[side.ordinal()];
    }

    public static void set(LuxSideMode[] sideModes, Direction side, LuxSideMode mode) {
        sideModes[side.ordinal()] = mode;
    }

    public static void load(CompoundTag tag, LuxSideMode[] sideModes, LuxSideMode defaultMode) {
        Arrays.fill(sideModes, defaultMode);
        if (!tag.contains(SIDE_MODES_KEY)) {
            return;
        }

        CompoundTag sideModeTag = tag.getCompound(SIDE_MODES_KEY);
        for (Direction direction : Direction.values()) {
            String key = direction.getSerializedName();
            if (sideModeTag.contains(key)) {
                sideModes[direction.ordinal()] = LuxSideMode.byName(sideModeTag.getString(key), defaultMode);
            }
        }
    }

    public static void save(CompoundTag tag, LuxSideMode[] sideModes) {
        CompoundTag sideModeTag = new CompoundTag();
        for (Direction direction : Direction.values()) {
            sideModeTag.putString(
                    direction.getSerializedName(),
                    sideModes[direction.ordinal()].name());
        }
        tag.put(SIDE_MODES_KEY, sideModeTag);
    }
}
