package de.zalando.util.web.urlmapping;

import de.zalando.util.web.urlmapping.rule.MappingRule;
import de.zalando.util.web.urlmapping.util.Helper;

public class UrlMappingException extends Exception {

    private static final long serialVersionUID = 8423378760909345402L;

    public UrlMappingException(final String message, final Throwable cause, final MappingContext context,
            final MappingRule rule) {
        super(message, cause);
        this.mappingRule = rule;
        this.originalUrl = Helper.getOriginalUrl(context.getRequest());
    }

    private final String originalUrl;

    public String getOriginalUrl() {
        return originalUrl;
    }

    private final MappingRule mappingRule;

    public MappingRule getMappingRule() {
        return mappingRule;
    }
}
