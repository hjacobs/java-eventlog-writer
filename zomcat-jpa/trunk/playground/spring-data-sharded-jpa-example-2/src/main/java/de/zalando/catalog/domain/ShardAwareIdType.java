package de.zalando.catalog.domain;

// ich habe keine Ahnung warum es keine 0 gibt
// was soll dass, wenn es in ShardedId.of(5) nicht funktioniert
public enum ShardAwareIdType {

    MULTIMEDIA_0(0),
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
