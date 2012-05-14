package de.zalando.util.web.urlmapping.param;

import java.util.Map;

import de.zalando.util.web.urlmapping.MappingContext;
import de.zalando.util.web.urlmapping.builder.UrlBuilder;

/**
 * A Handler that has full access to the {@link MappingContext}.
 *
 * @author  Sean Patrick Floyd (sean.floyd@zalando.de)
 */
public interface PostProcessor extends Handler {

    /**
     * Post-process the URLBuilder. If the handler maps a parameter to a value, it must add the key / value pair to the
     * supplied map.
     */
    void postProcess(UrlBuilder urlBuilder, MappingContext context, Map<String, String> map);
}
