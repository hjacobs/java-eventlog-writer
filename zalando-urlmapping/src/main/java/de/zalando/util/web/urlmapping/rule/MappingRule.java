package de.zalando.util.web.urlmapping.rule;

import java.io.Serializable;

import de.zalando.util.web.urlmapping.MappingContext;
import de.zalando.util.web.urlmapping.UrlMappingException;

/**
 * A rule that handles request mappings.
 *
 * @author  Sean Patrick Floyd (sean.floyd@zalando.de)
 */
public interface MappingRule extends Serializable {

    /**
     * Rule id.
     */
    String getId();

    /**
     * Check whether the rule can be applied to the current context.
     */
    boolean appliesTo(MappingContext mappingContext);

    /**
     * Apply the rule to the current context (this will usually result in a Forward or Redirect.
     *
     * @throws  UrlMappingException
     */
    void apply(MappingContext context) throws UrlMappingException;

}
