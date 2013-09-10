package de.zalando.jpa.example.article;

import java.io.Serializable;

import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.springframework.util.Assert;

/**
 * A composite-entity-id-class. In use for {@link ArticleConfig} and {@link ArticleModel}.<br/>
 * Make sure the annotated {@link Id}-field also annotated with {@link OneToOne} is named 'articlesku'.
 *
 * @author  jbellmann
 */
public class ArticleSkuPk implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Attention: name the corresponding {@link Id} fields exactly like this field.
     */
    private Long articlesku;

    /**
     * INTERNAL: Only for JPA.
     */
    protected ArticleSkuPk() { }

    public ArticleSkuPk(final Long articleskuid) {
        Assert.notNull(articleskuid, "ArticleSkuId should never be null");
        this.articlesku = articleskuid;
    }

    public static ArticleSkuPk build(final Long articleskuid) {
        return new ArticleSkuPk(articleskuid);
    }

    public static ArticleSkuPk build(final ArticleSku configSku) {
        return new ArticleSkuPk(configSku.getId());
    }

    @Override
    public int hashCode() {

        if (this.articlesku != null) {
            return this.articlesku.hashCode();
        }

        return 0;
    }

    @Override
    public boolean equals(final Object obj) {

        if (obj instanceof ArticleSkuPk) {
            final ArticleSkuPk other = (ArticleSkuPk) obj;
            return this.articlesku.equals(other.articlesku);
        }

        return false;
    }

    @Override
    public String toString() {
        return new StringBuilder("ArticleConfigPk[").append("configSkuId=").append(articlesku.toString()).append("]")
                                                    .toString();
    }

}
