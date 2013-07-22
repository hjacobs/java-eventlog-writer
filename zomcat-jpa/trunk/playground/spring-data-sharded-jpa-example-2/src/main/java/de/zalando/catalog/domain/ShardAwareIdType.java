package de.zalando.catalog.domain;

public enum ShardAwareIdType {

    MULTIMEDIA(1);

    private final int code;

    ShardAwareIdType(final int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static ShardAwareIdType fromCode(final int code) {
        for (final ShardAwareIdType shardAwareIdType : ShardAwareIdType.values()) {
            if (shardAwareIdType.code == code) {
                return shardAwareIdType;
            }
        }

        throw new IllegalArgumentException(String.format("Could not find ShardAwareIdType by code: %d", code));
    }

}
