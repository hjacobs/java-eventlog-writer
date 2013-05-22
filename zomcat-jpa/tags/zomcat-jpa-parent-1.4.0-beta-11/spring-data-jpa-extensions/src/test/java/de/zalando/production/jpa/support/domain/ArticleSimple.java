package de.zalando.production.jpa.support.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import de.zalando.data.annotation.SkuId;

/**
 * Author: clohmann Date: 06.05.13 Time: 18:17
 */
@Entity
@Table(name = "article_simple", schema = "zprod_data")
public class ArticleSimple {

    @Id
    @SkuId(value = "zprod_data.article_simple_id_seq", negate = true)
    Long id;
}
