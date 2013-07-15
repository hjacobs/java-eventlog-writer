package de.zalando.jpa.example.article;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

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

    public ArticleModel getArticleModel() {
        return articleModel;
    }

    public void setArticleModel(final ArticleModel articleModel) {
        this.articleModel = articleModel;
    }

    public ArticleSku getConfigSku() {
        return configSku;
    }

    public void setConfigSku(final ArticleSku configSku) {
        this.configSku = configSku;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }
}
