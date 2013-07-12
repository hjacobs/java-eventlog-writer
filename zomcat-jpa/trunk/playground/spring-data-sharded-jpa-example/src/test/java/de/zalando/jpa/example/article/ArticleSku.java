package de.zalando.jpa.example.article;

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

import de.zalando.sprocwrapper.sharding.ShardKey;

@Entity
@Table(name = "article_sku", schema = "zzj_data")
@Partitioning(name = "PartitionByShardKey", partitioningClass = PartitioningPolicyShardKey.class)
@Partitioned("PartitionByShardKey")
public class ArticleSku {

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
}
