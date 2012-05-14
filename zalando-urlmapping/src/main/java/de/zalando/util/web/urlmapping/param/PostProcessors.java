/**
 *
 */
package de.zalando.util.web.urlmapping.param;

import java.util.Map;

import de.zalando.util.web.urlmapping.MappingContext;
import de.zalando.util.web.urlmapping.builder.UrlBuilder;

/**
 * Factory methods for creating {@link PostProcessor} instances.
 *
 * @author  Sean Patrick Floyd (sean.floyd@zalando.de)
 */
public final class PostProcessors {

    private static class FixedFirstParameter implements PostProcessor {

        private static final long serialVersionUID = -9131221995205341534L;
        private final String name;
        private final String value;

        public FixedFirstParameter(final String name, final String value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public void postProcess(final UrlBuilder urlBuilder, final MappingContext context,
                final Map<String, String> map) {
            urlBuilder.setFirstParameter(name, value);
            // don't write to parameter map, as these are not incoming parameters
        }

    }

    private static final class FixedParameter implements PostProcessor {

        private final String name;
        private final String value;
        private static final long serialVersionUID = -1319154049058554744L;

        private FixedParameter(final String name, final String value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public void postProcess(final UrlBuilder urlBuilder, final MappingContext context,
                final Map<String, String> parameterMap) {
            urlBuilder.addParam(name, value);
            // don't write to parameter map, as these are not incoming parameters
        }
    }

    private PostProcessors() { }

    /**
     * Returns a {@link PostProcessor} that adds a key-value pair to the request.
     */
    public static PostProcessor addFixedParameterValue(final String name, final String value) {
        return new FixedParameter(name, value);
    }

    /**
     * Returns a {@link PostProcessor} that sets the specified key / value pair as the first request parameter.
     */
    public static PostProcessor addFirstParameterValue(final String name, final String value) {
        return new FixedFirstParameter(name, value);
    }

}
