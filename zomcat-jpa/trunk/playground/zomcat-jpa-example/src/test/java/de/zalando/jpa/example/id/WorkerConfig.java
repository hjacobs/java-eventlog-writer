package de.zalando.jpa.example.id;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.OneToOne;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

/**
 * @author  jbellmann
 */
@Entity
@IdClass(WorkerConfigPK.class)
public class WorkerConfig {

    @Id
    @OneToOne
    // optional, but should be used for correct zalando-table-names
    // @JoinColumn(name = "w_worker_id", referencedColumnName="w_id")
    private Worker worker;

    protected WorkerConfig() {
        // just for JPA
    }

    public WorkerConfig(final Worker worker) {
        Preconditions.checkNotNull(worker, "The worker should never be null");
        this.worker = worker;
    }

    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    @Override
    public int hashCode() {
        return this.worker.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof WorkerConfig)) {
            return false;
        }

        final WorkerConfig other = (WorkerConfig) obj;
        return this.worker.equals(other.worker);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("workerId", this.worker.getId()).toString();
    }

}
