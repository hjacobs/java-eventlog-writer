package de.zalando.sprocwrapper.dsprovider;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.zalando.zomcat.flowid.FlowPriority.Priority;

/**
 * @author  henning
 */
public class DataSourceProviderManagerTest {

    @Test(expected = NullPointerException.class)
    public void testWithoutProviders() {
        final DataSourceProviderManager mgr = new DataSourceProviderManager(null);
        Assert.assertNotNull(mgr);
    }

    @Test(expected = NullPointerException.class)
    public void testWithoutDefaultProvider() {
        final Map<Priority, DataSourceProvider> map = Maps.newHashMap();
        final DataSourceProviderManager mgr = new DataSourceProviderManager(map);
        Assert.assertNotNull(mgr);
    }

    @Test
    public void testWithDefaultProvider() {
        final Map<Priority, DataSourceProvider> map = Maps.newHashMap();
        map.put(Priority.DEFAULT, new DataSourceProvider() {

                @Override
                public DataSource getDataSource(final int i) {
                    return null;
                }

                @Override
                public List<Integer> getDistinctShardIds() {
                    return Lists.newArrayList(0);
                }
            });

        final DataSourceProviderManager mgr = new DataSourceProviderManager(map);
        Assert.assertEquals(null, mgr.getDataSource(0));
        Assert.assertEquals(1, mgr.getDistinctShardIds().size());
    }

}
