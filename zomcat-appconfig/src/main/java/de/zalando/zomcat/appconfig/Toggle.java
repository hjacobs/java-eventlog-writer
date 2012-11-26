package de.zalando.zomcat.appconfig;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;

public enum Toggle {

    ON(true),
    OFF(false);

    private final boolean value;

    private Toggle(final boolean value) {
        this.value = value;
    }

    public boolean asBoolean() {
        return value;
    }

    public static Toggle fromString(final String value) {
        checkArgument(!isNullOrEmpty(value), "value must not be null");
        if (ON.name().equalsIgnoreCase(value)) {
            return ON;
        }

        if (OFF.name().equalsIgnoreCase(value)) {
            return OFF;
        } else {
            throw new IllegalArgumentException(String.format("illegal value for toggle [%s]", value));
        }
    }

    public static Toggle fromBoolean(final boolean value) {
        return value ? ON : OFF;
    }
}
