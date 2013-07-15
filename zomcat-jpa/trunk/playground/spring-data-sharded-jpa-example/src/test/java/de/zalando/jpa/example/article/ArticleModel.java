package de.zalando.jpa.example.article;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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

    @ManyToMany
    @JoinTable
    private List<Attribute> attributes;

    public List<ArticleConfig> getArticleConfigs() {
        return articleConfigs;
    }

    public void setArticleConfigs(final List<ArticleConfig> articleConfigs) {
        this.articleConfigs = articleConfigs;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(final List<Attribute> attributes) {
        this.attributes = attributes;
    }

    public ArticleSku getModelSku() {
        return modelSku;
    }

    public void setModelSku(final ArticleSku modelSku) {
        this.modelSku = modelSku;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }
}
