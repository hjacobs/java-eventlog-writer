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
public class SeqIdEntityListener<T> {

    private static final Logger LOG = LoggerFactory.getLogger(SeqIdEntityListener.class);

    private SeqIdHandler<T> seqIdHandler;

    public SeqIdEntityListener() {
        LOG.debug("SkuIdEntityListner created");
    }

    public void setSkuIdHandler(final SeqIdHandler<T> keyHandler) {
        this.seqIdHandler = keyHandler;
    }

    @PrePersist
    public void touchForCreate(final Object target) {
        if (seqIdHandler != null) {
            seqIdHandler.markCreated(target);
        }
    }

}
