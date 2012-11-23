package de.zalando.zomcat.jobs.lock;

import org.junit.Test;

/**
 * @author  hjacobs
 */
public class LockResourceManagerImplTest {

    @Test(expected = IllegalArgumentException.class)
    public void testWithoutDataSourceProvider() {
        final LockResourceSprocService svc = new LockResourceManagerImpl(null);
    }

}
