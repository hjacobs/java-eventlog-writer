package de.zalando.jpa.example.id;

import java.io.Serializable;

import com.google.common.base.Objects;

/**
 * @author  jbellmann
 */
public class WorkerConfigPK implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long worker;

    public WorkerConfigPK() {
        //
    }

    public WorkerConfigPK(final Long workerId) {
        this.worker = workerId;
    }

    public static WorkerConfigPK build(final Worker worker) {
        return new WorkerConfigPK(worker.getId());
    }

    public static WorkerConfigPK build(final Long workerId) {
        return new WorkerConfigPK(workerId);
    }

    @Override
    public int hashCode() {
        if (this.worker != null) {

            return worker.hashCode();
        }

        return 0;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this.worker == null) {
            return false;
        }

        if (obj == null) {
            return false;
        }

        if (!(obj instanceof WorkerConfigPK)) {
            return false;
        }

        final WorkerConfigPK other = (WorkerConfigPK) obj;
        return worker.equals(other.worker);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("id", worker).toString();
    }

}
