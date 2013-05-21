package de.zalando.data.jpa.domain.support;

import javax.persistence.Entity;
import javax.persistence.PrePersist;

import org.eclipse.persistence.internal.jpa.metadata.listeners.EntityListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Configurable;

/**
 * {@link EntityListener} that injects a newly generated SkuId into marked {@link Entity}.
 *
 * @param   <T>
 *
 * @author  Joerg Bellmann
 */
@Configurable
public class SkuIdEntityListener<T> {

    private static final Logger LOG = LoggerFactory.getLogger(SkuIdEntityListener.class);

    private SkuIdHandler<T> skuIdHandler;

    public SkuIdEntityListener() {
        LOG.debug("SkuIdEntityListner created");
    }

    public void setSkuIdHandler(final SkuIdHandler<T> keyHandler) {
        this.skuIdHandler = keyHandler;
    }

    @PrePersist
    public void touchForCreate(final Object target) {
        if (skuIdHandler != null) {
            skuIdHandler.markCreated(target);
        }
    }

}
