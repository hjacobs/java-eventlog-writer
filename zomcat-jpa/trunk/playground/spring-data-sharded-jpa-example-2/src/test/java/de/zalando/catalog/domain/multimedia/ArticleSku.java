package de.zalando.catalog.domain.multimedia;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import de.zalando.catalog.domain.sku.ModelSku;
import de.zalando.catalog.domain.sku.SkuFactory;
import de.zalando.catalog.domain.sku.SkuType;

@Entity
@Table(schema = "zcat_data", name = "article_sku")
public class ArticleSku extends de.zalando.catalog.domain.sku.Sku implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Column
    private String sku;

    @Enumerated(EnumType.STRING)
    @Column(name = "sku_type")
    private SkuType type;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(final String sku) {
        this.sku = sku;
    }

    @Override
    public String asString() {
        return sku;
    }

    @Override
    public ModelSku getModelSku() {
        return SkuFactory.valueOf(sku).getModelSku();
    }

    public SkuType getType() {
        return type;
    }

    @Override
    public boolean isLegacySku() {
        return SkuFactory.valueOf(sku).isLegacySku();
    }

    @Override
    public int hashCode() {
        return SkuFactory.valueOf(sku).hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        return SkuFactory.valueOf(sku).equals(SkuFactory.valueOf((String) obj));
    }

    public void setType(final SkuType type) {
        this.type = type;
    }

    @Override
    public Object getShardKey() {
        return SkuFactory.valueOf(sku);
    }
}
