package de.zalando.jpa.example.article;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Partitioned;
import org.eclipse.persistence.annotations.ReplicationPartitioning;

import de.zalando.jpa.eclipselink.partitioning.annotations.CustomPartitioning;
import de.zalando.jpa.sharding.policy.ModuloPartitioningPolicy;

import de.zalando.sprocwrapper.sharding.ShardedObject;

@Entity
@Table(name = "article_sku", schema = "zzj_data")

// This is necessary for schema-generation on all shards.
@ReplicationPartitioning(
    name = ArticlePartitions.REPLICATE,
    connectionPools = {ArticlePartitions.Pools.DEFAULT, ArticlePartitions.Pools.NODE_2}
)

// this is new
@CustomPartitioning(
    name = ArticlePartitions.SHARDED_OBJECT_PARTITIONING, partitioningClass = ModuloPartitioningPolicy.class,
    unionUnpartitionableQueries = true
)
@Partitioned(ArticlePartitions.SHARDED_OBJECT_PARTITIONING)
public class ArticleSku implements ShardedObject, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "article_sku_id_seq")
    @SequenceGenerator(name = "article_sku_id_seq", sequenceName = "article_sku_id_seq", allocationSize = 1)
    private Long id;

// @ShardKey
    private String sku;

    @Enumerated(EnumType.STRING)
    private SkuType type;

    @ManyToOne
    private ArticleSku model;

    @ManyToOne
    private ArticleSku config;

    @Override
    public Object getShardKey() {
        return sku;
    }

    public ArticleSku getConfig() {
        return config;
    }

    public void setConfig(final ArticleSku config) {
        this.config = config;
    }

    public ArticleSku getModel() {
        return model;
    }

    public void setModel(final ArticleSku model) {
        this.model = model;
    }

    public Long getId() {
        return id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(final String sku) {
        this.sku = sku;
    }

    public SkuType getSkuType() {
        return type;
    }

    public void setSkuType(final SkuType skuType) {
        this.type = skuType;
    }
}
