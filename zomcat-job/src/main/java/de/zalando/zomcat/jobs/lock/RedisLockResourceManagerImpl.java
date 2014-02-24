package de.zalando.zomcat.jobs.lock;

import java.io.IOException;

import java.util.Date;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;

import de.zalando.zomcat.configuration.AppInstanceContextProvider;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisLockResourceManagerImpl extends AbstractLockResourceManager {

    private static final Logger LOG = LoggerFactory.getLogger(RedisLockResourceManagerImpl.class);

    public static final String KEY_SEPARATOR = ":";

    private final JedisPool redisPool;

    private final ObjectWriter jsonMapper;

    private final String host;
    private final String instanceCode;
    private final String lockKeyPrefix;

    public RedisLockResourceManagerImpl(final JedisPool redisPool, final ObjectMapper jsonMapper) {
        this.redisPool = Preconditions.checkNotNull(redisPool, "redisPool");
        this.jsonMapper = Preconditions.checkNotNull(jsonMapper, "jsonMapper").writer();

        final AppInstanceContextProvider appContextProvider = AppInstanceContextProvider.fromManifestOnFilesystem();
        this.host = appContextProvider.getHost();
        this.instanceCode = appContextProvider.getInstanceCode();
        this.lockKeyPrefix = new StringBuilder("job").append(KEY_SEPARATOR).append("lock").append(KEY_SEPARATOR)
                                                     .append(appContextProvider.getProjectName()).append(KEY_SEPARATOR)
                                                     .append(appContextProvider.getEnvironment()).toString();
        LOG.info(
            "Redis lock resource manager loaded on host: '{}', instance code: '{}' and using lock key prefix: '{}'",
            new Object[] {host, instanceCode, lockKeyPrefix});
    }

    @Override
    public boolean acquireLock(final String lockingComponent, final String resource, final String flowId,
            final long expectedMaximumDuration) {
        LOG.info("Acquiring lock on {} for {}", new String[] {resource, lockingComponent});

        final String key = buildKey(resource);
        try {
            final String value = buildPayload(lockingComponent, resource, expectedMaximumDuration);

            // setup everything before retrieving a connection from the pool
            final Jedis jedis = redisPool.getResource();
            try {
                return jedis.setnx(key, value) == 1;
            } finally {
                redisPool.returnResource(jedis);
            }
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    protected void doReleaseLock(final String resource, final String flowId) {
        final String key = buildKey(resource);

        final Jedis jedis = redisPool.getResource();
        try {
            jedis.del(key);
        } finally {
            redisPool.returnResource(jedis);
        }
    }

    @Override
    public boolean peekLock(final String resource) {
        final String key = buildKey(resource);

        final Jedis jedis = redisPool.getResource();
        try {
            return jedis.exists(key);
        } finally {
            redisPool.returnResource(jedis);
        }
    }

    public String getLockKeyPrefix() {
        return lockKeyPrefix;
    }

    private String buildKey(final String resource) {
        return new StringBuilder(lockKeyPrefix).append(KEY_SEPARATOR).append(resource).toString();
    }

    private String buildPayload(final String lockingComponent, final String resource,
            final long expectedMaximumDuration) throws IOException {

        return jsonMapper.writeValueAsString(new LockDetails(host, instanceCode, lockingComponent, resource,
                    expectedMaximumDuration));
    }

    protected static final class LockDetails {

        private final Date created = new Date();

        private final String host;
        private final String instanceCode;
        private final String lockingComponent;
        private final String resource;
        private final long expectedMaximumDuration;

        public LockDetails(final String host, final String instanceCode, final String lockingComponent,
                final String resource, final long expectedMaximumDuration) {
            this.host = host;
            this.instanceCode = instanceCode;
            this.lockingComponent = lockingComponent;
            this.resource = resource;
            this.expectedMaximumDuration = expectedMaximumDuration;
        }

        public Date getCreated() {
            return created;
        }

        public String getHost() {
            return host;
        }

        public String getInstanceCode() {
            return instanceCode;
        }

        public String getLockingComponent() {
            return lockingComponent;
        }

        public String getResource() {
            return resource;
        }

        public long getExpectedMaximumDuration() {
            return expectedMaximumDuration;
        }
    }
}
