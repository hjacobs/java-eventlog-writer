package de.zalando.jpa.eclipselink.partitioning.policies;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.persistence.descriptors.partitioning.PartitioningPolicy;
import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.sessions.server.ServerSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author  jbellmann
 */
public class ListPoolNamesPartitioningPolicy extends PartitioningPolicy {

    private static final long serialVersionUID = 1L;

    public static final String NAME = "listPoolNames";

    private final Logger LOG = LoggerFactory.getLogger(ListPoolNamesPartitioningPolicy.class);

    private final List<String> poolNames = new ArrayList<String>();

    public ListPoolNamesPartitioningPolicy() {
        setName(NAME);
    }

    @Override
    public void initialize(final AbstractSession session) {

        if (this.poolNames.isEmpty() && session.isServerSession()) {
            Set<String> poolNames = ((ServerSession) session).getConnectionPools().keySet();
            for (String poolName : poolNames) {
                LOG.info("FOUND POOL : {}", poolName);
                this.poolNames.add(poolName);
            }
        }
    }

    @Override
    public List<Accessor> getConnectionsForQuery(final AbstractSession session, final DatabaseQuery query,
            final AbstractRecord arguments) {
        return null;
    }

}
