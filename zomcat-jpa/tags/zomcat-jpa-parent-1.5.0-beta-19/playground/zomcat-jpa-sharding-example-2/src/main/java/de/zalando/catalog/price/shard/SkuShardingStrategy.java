package de.zalando.catalog.price.shard;

import com.google.common.base.Preconditions;

import de.zalando.catalog.domain.sku.Sku;

import de.zalando.sprocwrapper.sharding.VirtualShardMd5Strategy;

public class SkuShardingStrategy extends VirtualShardMd5Strategy {

    // ATTENTION: if the amont of shards change, adjust here.
    private static final int MASK = (1 << 3) - 1;

    @Override
    public int getShardId(final Object[] objs) {

        // FIXME: simple hack to handle non sharded sprocs.
        if (objs == null) {

            return 1;
        }

        final Object obj = objs[0];
        Preconditions.checkNotNull(obj);

        int shardId = -1;

        if (obj instanceof Sku) {

            final Sku sku = (Sku) obj;
            shardId = super.getShardId(new String[] {sku.getModelSku().asString()});

            // SORRY BUT THE DEPENDENCIES ARE NOT GOOD
// } else if (obj instanceof PriceDefinition) {
// final PriceDefinition pd = (PriceDefinition) obj;
// shardId = super.getShardId(new String[] {pd.getSku().getModelSku().asString()});
        } else if (obj instanceof String) {
            shardId = super.getShardId(new String[] {(String) obj});
        } else if (obj instanceof Integer) {
            shardId = (int) obj;
        } else {

            throw new IllegalArgumentException("unsupported type of given object");
        }

        return shardId;

    }

    /**
     * Gets the proper shard nr (1-8), 0 based.
     *
     * @param   objs
     *
     * @return
     */
    public int getConcreteShardId(final Object[] objs) {

        return getShardId(objs) & MASK;
    }
}
