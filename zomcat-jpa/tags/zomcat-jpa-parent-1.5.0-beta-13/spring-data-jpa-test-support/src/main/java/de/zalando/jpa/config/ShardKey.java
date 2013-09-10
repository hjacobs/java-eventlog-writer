package de.zalando.jpa.config;

/**
 * @author  jbellmann
 */
public final class ShardKey {

    private final String delimiter;

    public static final ShardKey ONE = new ShardKey("ONE");
    public static final ShardKey TWO = new ShardKey("TWO");

    private ShardKey(final String delimiter) {
        if (delimiter == null) {
            throw new IllegalArgumentException("ShardKeyDelimiter should not be null");
        }

        this.delimiter = delimiter;
    }

    @Override
    public int hashCode() {
        return this.delimiter.hashCode();
    }

    @Override
    public String toString() {
        return new StringBuilder().append("ShardKey[").append(delimiter).append("]").toString();
    }

    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }

        if (!(o instanceof ShardKey)) {
            return false;
        }

        final ShardKey other = (ShardKey) o;
        return this.delimiter.equals(other.delimiter);
    }
}
