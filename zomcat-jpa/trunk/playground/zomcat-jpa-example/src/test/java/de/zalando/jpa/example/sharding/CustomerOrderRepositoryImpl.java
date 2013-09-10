package de.zalando.jpa.example.sharding;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import de.zalando.jpa.config.ShardKey;

/**
 * Here it is possible to call methods of {@link JpaRepository} with a specific {@link ShardKey} argument.<br/>
 * This can then be set into an {@link ThreadLocal} to use it for DataSourceLookup.
 *
 * @author  jbellmann
 */
public class CustomerOrderRepositoryImpl implements CustomerOrderRepositoryCustom {

    @Autowired
    private JpaRepository<CustomerOrder, Long> jpaRepository;

    @Override
    public List<CustomerOrder> findAll(final ShardKey shardKey) {

        return this.jpaRepository.findAll();
    }

    @Override
    public List<CustomerOrder> findAll(final Sort sort, final ShardKey shardKey) {

        return this.jpaRepository.findAll(sort);
    }

    @Override
    public <S extends CustomerOrder> List<S> save(final Iterable<S> entities, final ShardKey shardKey) {

        return this.jpaRepository.save(entities);
    }

    @Override
    public void flush(final ShardKey shardKey) {

        this.jpaRepository.flush();
    }

    @Override
    public CustomerOrder saveAndFlush(final CustomerOrder entity, final ShardKey shardKey) {

        return this.jpaRepository.saveAndFlush(entity);
    }

    @Override
    public void deleteInBatch(final Iterable<CustomerOrder> entities, final ShardKey shardKey) {

        this.jpaRepository.deleteInBatch(entities);
    }

    @Override
    public void deleteAllInBatch(final ShardKey shardKey) {

        this.jpaRepository.deleteAllInBatch();
    }
}
