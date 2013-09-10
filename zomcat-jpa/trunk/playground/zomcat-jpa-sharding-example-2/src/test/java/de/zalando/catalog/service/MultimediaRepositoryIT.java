package de.zalando.catalog.service;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import de.zalando.catalog.backend.repository.ArticleSkuRepository;
import de.zalando.catalog.backend.repository.MultimediaRepository;
import de.zalando.catalog.domain.ShardedId;
import de.zalando.catalog.domain.multimedia.ArticleSku;
import de.zalando.catalog.domain.multimedia.MediaCharacter;
import de.zalando.catalog.domain.multimedia.MediaCharacterCode;
import de.zalando.catalog.domain.multimedia.Multimedia;
import de.zalando.catalog.domain.multimedia.MultimediaType;
import de.zalando.catalog.domain.multimedia.MultimediaTypeCode;
import de.zalando.catalog.domain.sku.Sku;
import de.zalando.catalog.service.article.MultimediaService;
import de.zalando.catalog.test.util.AbstractDBTest;

import de.zalando.jpa.config.ValueGenerator;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:backendContextTest.xml"})
public class MultimediaRepositoryIT extends AbstractDBTest {

    public static final Function<Multimedia, ShardedId> GET_IDS = new Function<Multimedia, ShardedId>() {
        @Override
        public ShardedId apply(final Multimedia input) {
            return input.getCode();
        }
    };

    public static final Function<Multimedia, Sku> GET_SKUS = new Function<Multimedia, Sku>() {
        @Override
        public Sku apply(final Multimedia input) {
            return input.getSku();
        }
    };

    public static final int MULTIMEDIA_PER_SKU = 3;

    @Autowired
    private MultimediaService multimediaService;

    @Autowired
    private MultimediaRepository multimediaRepository;

    @Autowired
    private ArticleSkuRepository articleSkuRepository;

    private List<ArticleSku> articleSkuList;

    private List<Multimedia> multimediaList;

    private MultimediaType multimediaType;
    private MediaCharacter mediaCharacter;

    @Before
    public void createCommons() {
        if (multimediaType == null) {
            multimediaType = new MultimediaType();
            multimediaType.setMimeType("image/jpeg");
            multimediaType.setMultimediaTypeCode(new MultimediaTypeCode("JPEG_IMAGE"));
            multimediaType.setName("Image JPEG");

            multimediaService.createOrUpdateMultimediaType(multimediaType);
        }

        if (mediaCharacter == null) {
            mediaCharacter = new MediaCharacter();
            mediaCharacter.setName("Image");
            mediaCharacter.setMediaCharacterCode(new MediaCharacterCode("IMAGE"));

            multimediaService.createOrUpdateMediaCharacter(mediaCharacter);
        }

        multimediaList = Lists.newArrayList();
        articleSkuList = Lists.newArrayList();

        for (int i = 0; i < MULTIMEDIA_PER_SKU; i++) {
            ArticleSku aSku = new ArticleSku();
            aSku.setSku(ValueGenerator.generateModelSKu());
            aSku = this.articleSkuRepository.save(aSku);
            articleSkuList.add(aSku);
        }

// for (int i = 0; i < 1; i++) {
//// final ArticleModel articleModel = articleITUtils.createArticleHierarchy();
// for (int j = 0; j < MULTIMEDIA_PER_SKU; j++) {
// final Multimedia multimedia = new Multimedia();
// multimedia.setPath("/test/path/model/" + j);
// multimedia.setTypeCode(multimediaType.getMultimediaTypeCode());
// multimedia.setMediaCharacterCode(mediaCharacter.getMediaCharacterCode());
//
//// multimedia.setSku(articleModel.getSku());
// multimedia.setSku(articleSkuList.get(j));
//
// final ShardedId code = multimediaService.createMultimedia(multimedia);
// multimedia.setCode(code);
// multimediaList.add(multimedia);
// }
// }

    }

    @Test
    public void readArticleSkus() {
        List<ArticleSku> articleSkuList = this.articleSkuRepository.findAll();
        Assert.assertNotNull(articleSkuList);
        Assert.assertFalse(articleSkuList.isEmpty());
    }

    @Test
    @Ignore
    public void test() {
// final List<Multimedia> all = multimediaRepository.findAll();
// final List<ShardedId> ids = Lists.transform(multimediaList, GET_IDS);
//
// Assertions.assertThat(all).onProperty("code").containsOnly(ids.toArray());
//
// for (final ShardedId id : ids) {
// final Multimedia m = multimediaRepository.withShardId(id);
// Assertions.assertThat(m).isNotNull();
// Assertions.assertThat(m.getCode()).isEqualTo(id);
// }

    }

}
