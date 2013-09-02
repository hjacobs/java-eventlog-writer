package de.zalando.catalog.service;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import de.zalando.catalog.domain.multimedia.ArticleSku;

/**
 * To search in Iterable.
 *
 * @author  jbellmann
 */
final class FindBySku implements Predicate<ArticleSku> {

    public final String sku;

    public FindBySku(final String sku) {
        this.sku = sku;
    }

    @Override
    public boolean apply(@Nullable final ArticleSku input) {
        return this.sku.equals(input.asString());
    }
}
