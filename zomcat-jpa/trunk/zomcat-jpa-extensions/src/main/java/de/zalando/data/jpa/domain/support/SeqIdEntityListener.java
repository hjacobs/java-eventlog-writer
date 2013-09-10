package de.zalando.data.jpa.domain.support;

import javax.persistence.Entity;
import javax.persistence.PrePersist;

import org.eclipse.persistence.internal.jpa.metadata.listeners.EntityListener;

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

    private SeqIdHandler<T> seqIdHandler;

    public void setSeqIdHandler(final SeqIdHandler<T> keyHandler) {
        this.seqIdHandler = keyHandler;
    }

    @PrePersist
    public void touchForCreate(final Object target) {
        if (seqIdHandler != null) {
            seqIdHandler.markCreated(target);
        }
    }

}
