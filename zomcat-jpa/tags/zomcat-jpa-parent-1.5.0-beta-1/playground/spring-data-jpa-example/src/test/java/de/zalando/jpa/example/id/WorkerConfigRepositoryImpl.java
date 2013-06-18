package de.zalando.jpa.example.id;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author  jbellmann
 */
public class WorkerConfigRepositoryImpl implements WorkerConfigRepositoryCustom {

    @Autowired
    private JpaRepository<WorkerConfig, WorkerConfigPK> workerConfigRepository;

    @Override
    public WorkerConfig findByWorker(final Worker worker) {
        return this.workerConfigRepository.findOne(WorkerConfigPK.build(worker));
    }

}
