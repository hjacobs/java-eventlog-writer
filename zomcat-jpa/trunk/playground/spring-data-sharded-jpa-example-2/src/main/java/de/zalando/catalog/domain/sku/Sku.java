package de.zalando.catalog.domain.sku;

import java.io.Serializable;

import de.zalando.sprocwrapper.sharding.ShardedObject;

public abstract class Sku implements ShardedObject, Serializable {
    private static final long serialVersionUID = -8976418565910313002L;

    public abstract String asString();

    public abstract ModelSku getModelSku();

    public abstract SkuType getType();

    public abstract boolean isLegacySku();

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(final Object obj);
}
