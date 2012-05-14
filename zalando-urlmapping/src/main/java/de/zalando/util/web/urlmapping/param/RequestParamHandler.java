package de.zalando.util.web.urlmapping.param;

import java.util.Map;

import de.zalando.util.web.urlmapping.RequestParamAware;
import de.zalando.util.web.urlmapping.builder.UrlBuilder;

public interface RequestParamHandler extends RequestParamAware, Handler {

    /**
     * Manipulate the outgoing URL based on incoming request parameters. If the handler maps a parameter to a value, it
     * must add the key / value pair to the supplied map.
     */
    void apply(Map<String, String[]> parameterMap, UrlBuilder urlBuilder, Map<String, String> outgoingMap);
}
