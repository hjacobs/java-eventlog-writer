package de.zalando.jpa.eclipselink.partitioning;

import java.util.List;

import org.eclipse.persistence.descriptors.partitioning.PartitioningPolicy;
import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.DatabaseQuery;

/**
 * Delegates to an injected {@link PartitioningPolicy}.<br/>
 * More or less identically to eclipselinks CustomPartitioningPolicy, but make sure initialization will also be
 * delegated.
 */
public class DelegatingPartitioningPolicy extends PartitioningPolicy {

    private PartitioningPolicy delegate;

    public DelegatingPartitioningPolicy(final String policyName) {
        setName(policyName);
    }

    public DelegatingPartitioningPolicy() { }

    /**
     * important, that is what not work in current implementation of eclipselink make sure the initialization will be
     * delegated to the delegate.
     */
    @Override
    public void initialize(final AbstractSession session) {
        if (delegate != null) {
            delegate.initialize(session);
        }
    }

    @Override
    public List<Accessor> getConnectionsForQuery(final AbstractSession session, final DatabaseQuery query,
            final AbstractRecord arguments) {
        if (delegate == null) {
            return null;
        }

        return delegate.getConnectionsForQuery(session, query, arguments);
    }

    public void setDelegate(final PartitioningPolicy delegate) {
        this.delegate = delegate;
    }

}
