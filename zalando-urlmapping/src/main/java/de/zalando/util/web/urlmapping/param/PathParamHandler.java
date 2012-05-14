package de.zalando.util.web.urlmapping.param;

import java.util.Map;

import de.zalando.util.web.urlmapping.builder.UrlBuilder;

/**
 * A handler that works on a path segment from the incoming URL.
 */
public interface PathParamHandler extends Handler {

    /**
     * Check whether this parameter handler applies to this path segment. In most cases this method should just return
     * true.
     */
    boolean appliesTo(String segment);

    /**
     * Process the path segment. If the handler maps a parameter to a value, it must add the key / value pair to the
     * supplied map.
     */
    void apply(String segment, UrlBuilder urlBuilder, Map<String, String> parameterRegistrationMap);

}
