package de.zalando.jpa.example.sharding;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import de.zalando.jpa.config.ShardKey;

/**
 * Extends all Method from an {@link JpaRepository} with an {@link ShardKey} argument.
 *
 * @author  jbellmann
 */
public interface CustomerOrderRepositoryCustom {

    List<CustomerOrder> findAll(ShardKey shardKey);

    List<CustomerOrder> findAll(Sort sort, ShardKey shardKey);

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#save(java.lang.Iterable)
     */
    <S extends CustomerOrder> List<S> save(Iterable<S> entities, ShardKey shardKey);

    /**
     * Flushes all pending changes to the database.
     */
    void flush(ShardKey shardKey);

    /**
     * Saves an entity and flushes changes instantly.
     *
     * @param   entity
     *
     * @return  the saved entity
     */
    CustomerOrder saveAndFlush(CustomerOrder entity, ShardKey shardKey);

    /**
     * Deletes the given entities in a batch which means it will create a single {@link Query}. Assume that we will
     * clear the {@link javax.persistence.EntityManager} after the call.
     *
     * @param  entities
     */
    void deleteInBatch(Iterable<CustomerOrder> entities, ShardKey shardKey);

    /**
     * Deletes all entites in a batch call.
     */
    void deleteAllInBatch(ShardKey shardKey);

}
