package de.zalando.catalog.backend;

import java.util.List;

import org.eclipse.persistence.descriptors.partitioning.PartitioningPolicy;
import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.DatabaseQuery;

// this seems more or less identically to eclipselinks CustomPartitioningPolicy
public class DelegatingPartitioningPolicy extends PartitioningPolicy {

    private PartitioningPolicy delegate;

    public DelegatingPartitioningPolicy(final String policiesName) {
        setName(policiesName);
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
