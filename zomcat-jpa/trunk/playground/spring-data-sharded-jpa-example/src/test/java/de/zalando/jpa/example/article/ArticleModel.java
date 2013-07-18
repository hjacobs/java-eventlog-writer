package de.zalando.jpa.example.article;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Partitioned;

import de.zalando.sprocwrapper.sharding.ShardedObject;

@Entity
@IdClass(ArticleSkuPk.class)
@Table(name = "article_model", schema = "zzj_data")
@Partitioned(ArticlePartitions.SHARDED_OBJECT_PARTITIONING)
public class ArticleModel implements ShardedObject {

    @Id
    @OneToOne
    private ArticleSku articlesku;

    private String name;

    @OneToMany
    private List<ArticleConfig> articleConfigs = new ArrayList<>();

    @ManyToMany
    @JoinTable
    private List<Attribute> attributes;

    /**
     * INTERNAL: Only for JPA.
     */
    protected ArticleModel() { }

    public ArticleModel(final ArticleSku articleSku) {
        this.articlesku = articleSku;
    }

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
        return articlesku;
    }

    public void setModelSku(final ArticleSku modelSku) {
        this.articlesku = modelSku;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Just delegates to the {@link ArticleSku}-id field to get the sku.
     */
    @Override
    public Object getShardKey() {
        return this.articlesku.getShardKey();
    }
}
