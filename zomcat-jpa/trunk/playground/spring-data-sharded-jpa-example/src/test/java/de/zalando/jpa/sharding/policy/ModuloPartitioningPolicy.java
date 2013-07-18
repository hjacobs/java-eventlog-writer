package de.zalando.jpa.sharding.policy;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.partitioning.PartitioningPolicy;
import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ObjectLevelModifyQuery;
import org.eclipse.persistence.sessions.server.ClientSession;
import org.eclipse.persistence.sessions.server.ServerSession;

import de.zalando.jpa.eclipselink.LogSupport;

import de.zalando.sprocwrapper.sharding.ShardedObject;

/**
 * Beispiel-PartitioninPolicy mit der wir ueben koennen.
 *
 * @author  jbellmann
 */
public class ModuloPartitioningPolicy extends PartitioningPolicy {

    private static final long serialVersionUID = 1L;

    private List<String> connectionPools = new ArrayList<String>();

    private boolean unionUnpartitionableQueries = true;

    public ModuloPartitioningPolicy() {
        super();
    }

    @Override
    public void initialize(final AbstractSession session) {

        super.initialize(session);
        if (getConnectionPools().isEmpty() && session.isServerSession()) {
            getConnectionPools().addAll(((ServerSession) session).getConnectionPools().keySet());
        }

        //
        // session.getProperty("anCustomPropertyName_FOR_STRATEGY_TO_PROCESS_ON_SKU");

        // and further customProperties
    }

    public List<String> getConnectionPools() {
        return this.connectionPools;
    }

    public void setConnectionPools(final List<String> connectionPools) {
        this.connectionPools = connectionPools;
    }

    public boolean isUnionUnpartitionableQueries() {
        return unionUnpartitionableQueries;
    }

    public void setUnionUnpartitionableQueries(final boolean unionUnpartitionableQueries) {
        this.unionUnpartitionableQueries = unionUnpartitionableQueries;
    }

    // TAKEN FROM HASHPARTITIONINGPOLICY
    @Override
    public List<Accessor> getConnectionsForQuery(final AbstractSession session, final DatabaseQuery query,
            final AbstractRecord arguments) {

        LogSupport.logFine(session, "getConnectionQuery");

        Object value = null;

        // yes, a query seems to modify objects, sounds a bit strange but it works
        if (ObjectLevelModifyQuery.class.isAssignableFrom(query.getClass())) {
            Object objectToModify = ((ObjectLevelModifyQuery) query).getObject();
            if (ShardedObject.class.isAssignableFrom(objectToModify.getClass())) {
                value = ((ShardedObject) objectToModify).getShardKey();
            }
        }

        LogSupport.logFine(session, "query : value {0}", value);

        if (value == null) {

            if (this.unionUnpartitionableQueries) {

                // Use all connections.

                LogSupport.logFine(session, "query : use all connections");

                List<Accessor> accessors = new ArrayList<Accessor>(this.connectionPools.size());
                for (String poolName : this.connectionPools) {
                    accessors.add(getAccessor(poolName, session, query, false));
                }

                return accessors;
            } else {

                // Use default behavior.

                LogSupport.logFine(session, "query : use default");

                return null;
            }
        }

        // TODO, we have the value (shardKey), how to decide which shard ?
        // is there a strategy defined somewhere ?
        int index = value.toString().length() % 2;

        LogSupport.logFine(session, "query : value {0} --> index {1}", value, index);

        if (session.getPlatform().hasPartitioningCallback()) {

            // UCP support.
            session.getPlatform().getPartitioningCallback().setPartitionId(index);
            return null;
        }

        // Use the mapped connection pool.
        List<Accessor> accessors = new ArrayList<Accessor>(1);
        String poolName = this.connectionPools.get(index);

        LogSupport.logFine(session, "query : selected poolName {0}", poolName);

        accessors.add(getAccessor(poolName, session, query, false));
        return accessors;
    }

    // TAKEN FROM HASHPARTITIONINGPOLICY
    @Override
    public void partitionPersist(final AbstractSession session, final Object object, final ClassDescriptor descriptor) {

        // SCHEINBAR DOCH,
        // throw new RuntimeException("HIER KOMMEN WIR SCHEINBAR NIE REIN");

        Object value = this.extractPartitionValueForPersist(session, object, descriptor);
        if (value == null) {
            return;
        }

        // int index = value.hashCode() % this.connectionPools.size();
        int index = value.toString().length() % 2;

        LogSupport.logFine(session, "persist : value {0} --> index {1}", value, index);

        if (session.getPlatform().hasPartitioningCallback()) {

            // UCP support.
            session.getPlatform().getPartitioningCallback().setPartitionId(index);
        } else {
            String poolName = this.connectionPools.get(index);

            LogSupport.logFine(session, "persist : selected poolName {0}", poolName);

            getAccessor(poolName, session, null, false);
        }
    }

    // TAKEN FROM FIELDPARTITIONINGPOLICY
    /**
     * INTERNAL: If persist should be partitioned, extra value from object.
     */
    public Object extractPartitionValueForPersist(final AbstractSession session, final Object object,
            final ClassDescriptor descriptor) {
        if (!session.isClientSession()) {
            return null;
        }

        ClientSession client = (ClientSession) session;

        // Only assign the connection if exclusive.
        if (!client.isExclusiveIsolatedClientSession() || client.hasWriteConnection()) {
            return null;
        }

// return descriptor.getObjectBuilder().extractValueFromObjectForField(object, this.partitionField, session);

        return this.extractValueFromObjectForField(object, session);
    }

    // TO INSPECT OBJECT
    protected Object extractValueFromObjectForField(final Object object, final AbstractSession session) {

        if (ShardedObject.class.isAssignableFrom(object.getClass())) {
            return ((ShardedObject) object).getShardKey();
        }

        return null;
    }

}
