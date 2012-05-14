package de.zalando.util.web.urlmapping.rule;

import java.io.Serializable;

import de.zalando.util.web.urlmapping.MappingContext;
import de.zalando.util.web.urlmapping.UrlMappingException;

public interface MappingRule extends Serializable {
    String getId();

    boolean appliesTo(MappingContext mappingContext);

    void apply(MappingContext context) throws UrlMappingException;

}
