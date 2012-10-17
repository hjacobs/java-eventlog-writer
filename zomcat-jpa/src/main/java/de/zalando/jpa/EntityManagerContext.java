package de.zalando.jpa;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class EntityManagerContext {
    @PersistenceContext
    private EntityManager em;

    protected EntityManager em() {
        return em;
    }
}
