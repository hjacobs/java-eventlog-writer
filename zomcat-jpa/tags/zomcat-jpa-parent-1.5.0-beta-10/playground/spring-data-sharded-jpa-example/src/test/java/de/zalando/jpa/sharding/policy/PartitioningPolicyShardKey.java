package de.zalando.jpa.sharding.policy;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.partitioning.FieldPartitioningPolicy;
import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.sessions.server.ServerSession;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * PUBLIC: HashPartitioningPolicy partitions access to a database cluster by the hash of a field value from the object,
 * such as the object's location, or tenant. The hash indexes into the list of connection pools. All write or read
 * request for object's with that hash value are sent to the server. If a query does not include the field as a
 * parameter, then it can either be sent to all server's and unioned, or left to the sesion's default behavior.
 *
 * @author  James Sutherland
 * @since   EclipseLink 2.2
 */
public class PartitioningPolicyShardKey extends FieldPartitioningPolicy {

    protected List<String> connectionPools = Lists.newArrayList("default", "node2");

    public PartitioningPolicyShardKey() {
        super();
    }

    public PartitioningPolicyShardKey(final String partitionField) {
        super(partitionField);
    }

    public PartitioningPolicyShardKey(final String partitionField, final boolean unionUnpartitionableQueries) {
        super(partitionField, unionUnpartitionableQueries);
    }

    /**
     * INTERNAL: Default the connection pools to all pools if unset.
     */
    public void initialize(final AbstractSession session) {
        super.initialize(session);
        if (getConnectionPools().isEmpty() && session.isServerSession()) {
            getConnectionPools().addAll(((ServerSession) session).getConnectionPools().keySet());
        }
    }

    /**
     * PUBLIC: Return the list of connection pool names to replicate queries to.
     */
    public List<String> getConnectionPools() {
        return connectionPools;
    }

    /**
     * PUBLIC: Set the list of connection pool names to replicate queries to. A connection pool with the same name must
     * be defined on the ServerSession.
     */
    public void setConnectionPools(final List<String> connectionPools) {
        this.connectionPools = connectionPools;
    }

    /**
     * PUBLIC: Add the connection pool name to the list of pools to rotate queries through.
     */
    public void addConnectionPool(final String connectionPool) {
        getConnectionPools().add(connectionPool);
    }

    /**
     * INTERNAL: Get a connection from one of the pools in a round robin rotation fashion.
     */
    public List<Accessor> getConnectionsForQuery(final AbstractSession session, final DatabaseQuery query,
            final AbstractRecord arguments) {
        final Optional databaseField = Iterables.tryFind(arguments.keySet(), new Predicate<DatabaseField>() {
                    @Override
                    public boolean apply(final DatabaseField input) {
                        return input.getName().endsWith("as_sku");
                    }
                });

        Object value = null;
        if (databaseField.isPresent()) {
            value = arguments.get(databaseField.get());
        }

        if (value == null) {
            if (this.unionUnpartitionableQueries) {

                // Use all connections.
                List<Accessor> accessors = new ArrayList<Accessor>(this.connectionPools.size());
                for (String poolName : this.connectionPools) {
                    accessors.add(getAccessor(poolName, session, query, false));
                }

                return accessors;
            } else {

                // Use default behavior.
                return null;
            }
        }

        int index = getShardIndex(value);

        if (session.getPlatform().hasPartitioningCallback()) {

            // UCP support.
            session.getPlatform().getPartitioningCallback().setPartitionId(index);
            return null;
        }

        // Use the mapped connection pool.
        List<Accessor> accessors = new ArrayList<Accessor>(1);
        String poolName = this.connectionPools.get(index);
        accessors.add(getAccessor(poolName, session, query, false));
        return accessors;
    }

    public int getShardIndex(final Object value) {
        if (value instanceof Number) {
            Number n = (Number) value;
            return n.intValue() % this.connectionPools.size();
        }

        return value.hashCode() % this.connectionPools.size();
    }

    /**
     * INTERNAL: Allow for the persist call to assign the partition.
     */
    @Override
    public void partitionPersist(final AbstractSession session, final Object object, final ClassDescriptor descriptor) {
        Object value = extractPartitionValueForPersist(session, object, descriptor);
        if (value == null) {
            return;
        }

        int index = value.hashCode() % this.connectionPools.size();
        if (session.getPlatform().hasPartitioningCallback()) {

            // UCP support.
            session.getPlatform().getPartitioningCallback().setPartitionId(index);
        } else {
            String poolName = this.connectionPools.get(index);
            getAccessor(poolName, session, null, false);
        }
    }
}
