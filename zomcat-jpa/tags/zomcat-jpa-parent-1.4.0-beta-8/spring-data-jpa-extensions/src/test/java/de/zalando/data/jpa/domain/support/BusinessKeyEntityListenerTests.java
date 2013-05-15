package de.zalando.data.jpa.domain.support;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.springframework.transaction.annotation.Transactional;

import de.zalando.data.jpa.domain.sample.AnnotatedProduct;
import de.zalando.data.jpa.domain.sample.BusinessKeyAwareProduct;
import de.zalando.data.jpa.domain.sample.BusinessKeyGeneratorStub;
import de.zalando.data.jpa.repository.sample.AnnotatedKeyableProductRepository;
import de.zalando.data.jpa.repository.sample.KeyableProductRepository;

/**
 * Integration test for {@link BusinessKeyEntityListener}.
 *
 * @author  jbellmann
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:businesskey/businesskey-entity-listener.xml")
@Transactional
@DirtiesContext
public class BusinessKeyEntityListenerTests {

    @Autowired
    KeyableProductRepository repository;

    @Autowired
    AnnotatedKeyableProductRepository annotatedRepository;

    @Autowired
    BusinessKeyGeneratorStub keyGenerator;

    BusinessKeyAwareProduct product;

    AnnotatedProduct annotatedProduct;

    @Autowired
    BusinessKeyHandler<?> keyHandler;

    @Before
    public void setUp() {
        assertNotNull(keyHandler);
        product = new BusinessKeyAwareProduct();
        repository.save(product);

        //
        annotatedProduct = new AnnotatedProduct();
        annotatedRepository.save(annotatedProduct);
    }

    @Test
    public void auditsRootEntityCorrectly() throws Exception {

        assertKeySet(product);

        //
        assertAnnotatedKeySet(annotatedProduct);
        assertCreatorSet(annotatedProduct);
        assertAuditorSet(annotatedProduct);
        assertCreationDateSet(annotatedProduct);
        assertLastModifiedDateSet(annotatedProduct);
    }

    private void assertLastModifiedDateSet(final AnnotatedProduct annotatedProduct2) {
        assertThat(annotatedProduct2.getLastModified(), is(notNullValue()));

    }

    private void assertCreationDateSet(final AnnotatedProduct annotatedProduct2) {
        assertThat(annotatedProduct2.getCreated(), is(notNullValue()));
    }

    private void assertAuditorSet(final AnnotatedProduct annotatedProduct) {
        assertThat(annotatedProduct.getLastModifiedBy(), is(notNullValue()));
    }

    private void assertCreatorSet(final AnnotatedProduct annotatedProduct) {
        assertThat(annotatedProduct.getCreatedBy(), is(notNullValue()));
    }

    private static void assertKeySet(final BusinessKeyAware keyable) {
        assertThat(keyable.getBusinessKeySelector(), is(notNullValue()));
        assertThat(keyable.getBusinessKey(), is(notNullValue()));
    }

    private static void assertAnnotatedKeySet(final AnnotatedProduct product) {
        assertThat(product.getBusinessKey(), is(notNullValue()));
    }
}
