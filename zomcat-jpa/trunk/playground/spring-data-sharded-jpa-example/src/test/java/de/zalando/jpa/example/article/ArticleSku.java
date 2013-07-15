package de.zalando.jpa.example.article;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Partitioned;
import org.eclipse.persistence.annotations.Partitioning;

import de.zalando.jpa.sharding.policy.PartitioningPolicyShardKey;

import de.zalando.sprocwrapper.sharding.ShardKey;
import de.zalando.sprocwrapper.sharding.ShardedObject;

@Entity
@Table(name = "article_sku", schema = "zzj_data")
@Partitioning(name = "PartitionByShardKey", partitioningClass = PartitioningPolicyShardKey.class)
@Partitioned("PartitionByShardKey")
public class ArticleSku implements ShardedObject, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private Integer id;

    @ShardKey
    @Column
    private String sku;

    @Enumerated(EnumType.STRING)
    @Column
    private SkuType skuType;

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

    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public ArticleSku getModel() {
        return model;
    }

    public void setModel(final ArticleSku model) {
        this.model = model;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(final String sku) {
        this.sku = sku;
    }

    public SkuType getSkuType() {
        return skuType;
    }

    public void setSkuType(final SkuType skuType) {
        this.skuType = skuType;
    }
}
