package de.zalando.data.jpa.domain.support;

import javax.persistence.Entity;
import javax.persistence.PrePersist;

import org.eclipse.persistence.internal.jpa.metadata.listeners.EntityListener;

import org.springframework.beans.factory.annotation.Configurable;

/**
 * {@link EntityListener} that injects a newly generated BusinessKey into marked {@link Entity}.
 *
 * @param   <T>
 *
 * @author  Joerg Bellmann
 */
@Configurable
public class BusinessKeyEntityListener<T> {

    private BusinessKeyHandler<T> keyHandler;

    public void setKeyHandler(final BusinessKeyHandler<T> keyHandler) {
        this.keyHandler = keyHandler;
    }

    @PrePersist
    public void touchForCreate(final Object target) {
        if (keyHandler != null) {
            keyHandler.markCreated(target);
        }
    }

}
