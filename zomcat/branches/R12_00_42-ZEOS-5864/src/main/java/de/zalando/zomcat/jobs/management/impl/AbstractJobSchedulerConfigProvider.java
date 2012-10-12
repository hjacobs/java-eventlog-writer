package de.zalando.zomcat.jobs.management.impl;

import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import de.zalando.zomcat.jobs.management.JobSchedulingConfigurationProvider;

/**
 * Abstract Base Class for all {@link JobSchedulingConfigurationProvider} implementations. Offers convenience methods
 * for processing Scheduler Configurations provided by the actual {@link JobSchedulingConfigurationProvider}
 * implementation derived from this class
 *
 * @author  Thomas Zirke (thomas.zirke@zalando.de)
 */
public abstract class AbstractJobSchedulerConfigProvider implements JobSchedulingConfigurationProvider {

    protected static final long getMillis(final String s) {
        final int len = s.length();
        if (s.endsWith("h")) {

            // hours
            return 60 * 60 * 1000 * Long.valueOf(s.substring(0, len - 1));
        } else if (s.endsWith("m")) {

            // minutes
            return 60 * 1000 * Long.valueOf(s.substring(0, len - 1));
        } else if (s.endsWith("s")) {

            // seconds
            return 1000 * Long.valueOf(s.substring(0, len - 1));
        }

        // millis
        return Long.valueOf(s);
    }

    protected static Map<String, String> getJobData(final String[] cols, final int startCol) {
        final Map<String, String> map = Maps.newHashMap();
        for (int i = startCol; i < cols.length; i++) {
            final String[] keyValue = cols[i].split("=");
            Preconditions.checkElementIndex(1, keyValue.length, "invalid key=value pair in job data");
            map.put(keyValue[0], keyValue[1]);
        }

        return map;
    }
}
