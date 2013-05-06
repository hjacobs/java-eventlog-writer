package de.zalando.util.web.urlmapping.param;

import java.util.Map;

import de.zalando.util.web.urlmapping.MappingContext;

/**
 * A handler that operates on the raw internal URL after all other handler types have run through.
 */
public interface UrlPostProcessor extends Handler {

    /**
     * Replace the existing internal URL with a new one.
     */
    String postProcess(String url, MappingContext context, Map<String, String> parameterMap);

}
