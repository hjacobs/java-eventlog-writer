package de.zalando.catalog.backend;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

import javax.sql.DataSource;

import org.eclipse.persistence.descriptors.partitioning.PartitioningPolicy;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.sessions.Connector;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.JNDIConnector;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.server.ExternalConnectionPool;
import org.eclipse.persistence.sessions.server.ServerSession;

import org.springframework.jdbc.datasource.lookup.DataSourceLookup;
import org.springframework.jdbc.datasource.lookup.MapDataSourceLookup;

import org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager;

import de.zalando.jpa.springframework.ExtendedEclipseLinkJpaVendorAdapter;

public class ShardedEclipseLinkJpaVendor extends ExtendedEclipseLinkJpaVendorAdapter {

    private DefaultPersistenceUnitManager persistenceUnitManager;
    private List<PartitioningPolicy> partitioningPolicies;

    public ShardedEclipseLinkJpaVendor(final DefaultPersistenceUnitManager persistenceUnitManager) {
        this.persistenceUnitManager = persistenceUnitManager;
    }

    @Override
    public void postProcessEntityManagerFactory(final EntityManagerFactory coreEntityManagerFactory) {
        super.postProcessEntityManagerFactory(coreEntityManagerFactory);

        if (coreEntityManagerFactory instanceof EntityManagerFactoryImpl) {
            final EntityManagerFactoryImpl entityManagerFactory = (EntityManagerFactoryImpl) coreEntityManagerFactory;
            final DataSourceLookup dataSourceLookup = persistenceUnitManager.getDataSourceLookup();

            final ServerSession serverSession = entityManagerFactory.getServerSession();

            if (dataSourceLookup instanceof MapDataSourceLookup) {
                final Map<String, DataSource> dataSources = ((MapDataSourceLookup) dataSourceLookup).getDataSources();
                for (final Map.Entry<String, DataSource> dataSourceEntry : dataSources.entrySet()) {
                    final String dataSourceName = dataSourceEntry.getKey();
                    final DataSource dataSource = dataSourceEntry.getValue();
                    final ExternalConnectionPool pool = getConnectionPool(serverSession, dataSourceName, dataSource);
                    serverSession.addConnectionPool(pool);
                    pool.startUp();
                }
            }

            final Project project = serverSession.getProject();
            for (final PartitioningPolicy partitioningPolicy : this.partitioningPolicies) {
                final PartitioningPolicy delegatingPolicy = project.getPartitioningPolicy(partitioningPolicy.getName());
                if (delegatingPolicy != null && delegatingPolicy instanceof DelegatingPartitioningPolicy) {
                    ((DelegatingPartitioningPolicy) delegatingPolicy).setDelegate(partitioningPolicy);
                }
            }

        }
    }

    private ExternalConnectionPool getConnectionPool(final ServerSession serverSession, final String dataSourceName,
            final DataSource dataSource) {
        final DatabaseLogin login = getDatabaseLogin(dataSource);
        login.useExternalConnectionPooling();
        return new ExternalConnectionPool(dataSourceName, login, serverSession);
    }

    private DatabaseLogin getDatabaseLogin(final DataSource dataSource) {
        final Connector connector = new JNDIConnector(dataSource);
        final DatabaseLogin login = new DatabaseLogin();
        login.setConnector(connector);
        return login;
    }

    public void setPartitioningPolicies(final List<PartitioningPolicy> partitioningPolicies) {
        this.partitioningPolicies = partitioningPolicies;
    }
}
