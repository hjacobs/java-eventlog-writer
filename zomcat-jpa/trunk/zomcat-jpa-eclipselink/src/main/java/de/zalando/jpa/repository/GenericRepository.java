package de.zalando.jpa.repository;

import java.util.List;

public interface GenericRepository<T> {

    T get(Integer id);

    List<T> getAll();

    void save(T object);

    T merge(T object);

    void delete(T object);
}
