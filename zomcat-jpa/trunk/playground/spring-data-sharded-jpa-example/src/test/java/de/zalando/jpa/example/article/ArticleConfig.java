package de.zalando.jpa.example.article;

import javax.persistence.*;

import org.eclipse.persistence.annotations.Partitioned;

@Entity
@Table(name = "article_config", schema = "zzj_data")
@Partitioned("PartitionByShardKey")
public class ArticleConfig {

    @Id
    @OneToOne
    private ArticleSku configSku;

    @Column
    private String name;

    @ManyToOne
    private ArticleModel articleModel;
}
