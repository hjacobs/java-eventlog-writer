package de.zalando.catalog.domain;

import java.io.Serializable;

import java.nio.ByteBuffer;

import com.google.common.base.Preconditions;
import com.google.common.primitives.Longs;

import de.zalando.sprocwrapper.sharding.ShardedObject;

// @XmlJavaTypeAdapter(ShardedIdAdapter.class)
public class ShardedId implements ShardedObject, Serializable {

    private static final long serialVersionUID = 1L;

    private final long id;
    private final ShardAwareIdType shardAwareIdType;
    private final int virtualShardId;
    private final int sequence;

    private ShardedId(final long id, final ShardAwareIdType shardAwareIdType, final int virtualShardId,
            final int sequence) {
        Preconditions.checkNotNull(shardAwareIdType);
        this.id = id;
        this.shardAwareIdType = shardAwareIdType;
        this.virtualShardId = virtualShardId;
        this.sequence = sequence;
    }

    public long asLong() {
        return id;
    }

    public ShardAwareIdType getType() {
        return shardAwareIdType;
    }

    public int getVirtualShardId() {
        return virtualShardId;
    }

    public int getSequence() {
        return sequence;
    }

    public static ShardedId of(final long id) {
        final byte[] bytes = Longs.toByteArray(id);

        final int typeCode = getInt(bytes, 0, 1);
        final int shardId = getInt(bytes, 1, 3);
        final int sequence = getInt(bytes, 4, 4);

        final ShardAwareIdType type = ShardAwareIdType.fromCode(typeCode);
        return new ShardedId(id, type, shardId, sequence);
    }

    private static int getInt(final byte[] src, final int index, final int length) {
        final int byteSizeOfInt = 4;
        if (length > byteSizeOfInt || index >= src.length || length + index > src.length || index < 0 || length <= 0) {
            throw new IllegalArgumentException();
        }

        final byte[] bytes = new byte[byteSizeOfInt];
        System.arraycopy(src, index, bytes, bytes.length - length, length);
        return ByteBuffer.wrap(bytes).getInt();
    }

    @Override
    public Object getShardKey() {
        return virtualShardId;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ShardedId{");
        sb.append("id=").append(id);
        sb.append(", shardAwareIdType=").append(shardAwareIdType);
        sb.append(", virtualShardId=").append(virtualShardId);
        sb.append(", sequence=").append(sequence);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ShardedId shardedId = (ShardedId) o;

        return id == shardedId.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

}
