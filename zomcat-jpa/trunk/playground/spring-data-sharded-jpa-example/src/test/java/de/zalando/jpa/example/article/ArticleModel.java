package de.zalando.jpa.example.article;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Partitioned;

@Entity
@Table(name = "article_model", schema = "zzj_data")
@Partitioned("PartitionByShardKey")
public class ArticleModel {

    @Id
    @OneToOne
    private ArticleSku modelSku;

    @Column
    private String name;

    @OneToMany
    private List<ArticleConfig> articleConfigs = new ArrayList<>();
}
