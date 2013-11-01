package de.zalando.zomcat.cxf.metrics;

import java.util.concurrent.TimeUnit;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;

public class JMXMetricsPublisher {

    public static JmxReporter createInstance(final String domain, final MetricRegistry registry) {
        return JmxReporter.forRegistry(registry).convertDurationsTo(TimeUnit.SECONDS).build();
    }

}
