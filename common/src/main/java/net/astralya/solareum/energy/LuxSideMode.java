package net.astralya.solareum.energy;

public enum LuxSideMode {
    NONE,
    INPUT,
    OUTPUT,
    BOTH;

    public boolean allowsInput() {
        return this == INPUT || this == BOTH;
    }

    public boolean allowsOutput() {
        return this == OUTPUT || this == BOTH;
    }

    public LuxSideMode next() {
        return switch (this) {
            case NONE -> INPUT;
            case INPUT -> OUTPUT;
            case OUTPUT -> BOTH;
            case BOTH -> NONE;
        };
    }

    public static LuxSideMode byName(String name, LuxSideMode fallback) {
        for (LuxSideMode mode : values()) {
            if (mode.name().equals(name)) {
                return mode;
            }
        }

        return fallback;
    }
}
