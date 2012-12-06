package de.zalando.jpa.repository.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.transaction.annotation.Transactional;

import de.zalando.jpa.repository.GenericRepository;

public abstract class GenericRepositoryImpl<T> implements GenericRepository<T> {

    private final Class<T> type;

    @PersistenceContext
    protected EntityManager entityManager;

    public GenericRepositoryImpl(final Class<T> type) {
        super();

        this.type = type;
    }

    @Override
    @Transactional(readOnly = true)
    public T get(final Integer id) {
        if (id == null) {
            return null;
        } else {
            return entityManager.find(type, id);
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public List<T> getAll() {
        return this.entityManager.createQuery("select o from " + this.type.getName() + " o").getResultList();
    }

    @Override
    @Transactional
    public void save(final T object) {
        fillBusinessKey(object);
        this.entityManager.persist(object);
    }

    @Override
    @Transactional
    public void delete(final T object) {

        // entity must be managed to call remove
        final T target = merge(object);

        this.entityManager.remove(target);
    }

    @Override
    @Transactional
    public T merge(final T object) {
        return this.entityManager.merge(object);
    }

    /**
     * Adds a value in the business key field of the entity (if it is empty). Override this in concrete repositories!
     * Default implementation does nothing.
     */
    protected void fillBusinessKey(final T entity) { }

}
