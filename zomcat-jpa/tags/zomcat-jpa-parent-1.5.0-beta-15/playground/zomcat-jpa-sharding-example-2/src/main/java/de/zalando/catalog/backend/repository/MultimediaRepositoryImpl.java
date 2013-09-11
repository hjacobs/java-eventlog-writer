package de.zalando.catalog.backend.repository;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.util.Assert;

import de.zalando.catalog.domain.ShardedId;
import de.zalando.catalog.domain.multimedia.ArticleSku;
import de.zalando.catalog.domain.multimedia.Multimedia;

/**
 * It is important to name this class {@link MultimediaRepository}Impl. Never name it {@link MultimediaRepositoryCustom}
 * Impl, that will not work. Spring-Data then tries to find properties named like methods.
 *
 * @author  jbellmann
 */
public class MultimediaRepositoryImpl implements MultimediaRepositoryCustom {

    @Autowired
    private MultimediaRepository multimediaRepository;

    @Autowired
    private ArticleSkuRepository articleSkuRepository;

    @Override
    public Multimedia findByShardedId(final ShardedId shardedId) {

        return multimediaRepository.findOne(shardedId.asLong());
    }

    @Override
    public Multimedia save(final Multimedia multimedia, final String sku) {
        Assert.hasText(sku, "Sku should never be null or empty");

        // maybe we can cache this, maybe it should not be an repo more an service with @Cacheable annotation
        ArticleSku articleSku = this.articleSkuRepository.findOneBySku(sku);

        // or we can add @NotNull on the field
        Assert.notNull(articleSku, "No ArticleSku found for 'sku' " + sku);
        multimedia.setSku(articleSku);

        return this.multimediaRepository.save(multimedia);

// final Multimedia result = this.multimediaRepository.saveAndFlush(multimedia);
// result.setSku(SkuFactory.valueOf(multimedia.getSku().asString()));
// result.setSku(SkuFactory.valueOf(sku));
// return result;
    }

}
