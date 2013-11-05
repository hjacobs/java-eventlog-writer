package de.zalando.zomcat.cxf.metrics;

import java.util.concurrent.TimeUnit;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;

/**
 * A utility class for registering a JMX Bean for metrics collection.
 *
 * @author  rreis
 */
public class JMXMetricsPublisher {

    /**
     * Creates an instance configured with the specified domain and registry.
     *
     * @param   domain    the domain used in JMX
     * @param   registry  the registry used to record metrics.
     *
     * @return
     */
    public static JmxReporter createInstance(final String domain, final MetricRegistry registry) {
        return JmxReporter.forRegistry(registry).inDomain(domain).convertDurationsTo(TimeUnit.SECONDS).build();
    }

}
