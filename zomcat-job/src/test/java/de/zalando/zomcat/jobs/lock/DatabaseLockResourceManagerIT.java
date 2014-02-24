package de.zalando.zomcat.jobs.lock;

import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:backendContextTest.xml"})
public class DatabaseLockResourceManagerIT extends AbstractLockResourceManagerIT {

    @Autowired
    private LockResourceManagerImpl lockResourceManager;

    @Override
    protected LockResourceManager getLockResourceManager() {
        return lockResourceManager;
    }

}
