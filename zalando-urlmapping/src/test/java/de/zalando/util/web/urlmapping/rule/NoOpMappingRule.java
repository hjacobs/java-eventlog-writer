package de.zalando.util.web.urlmapping.rule;

import de.zalando.util.web.urlmapping.MappingContext;

public abstract class NoOpMappingRule implements MappingRule {

    private static final long serialVersionUID = -7762052315689490623L;

    @Override
    public boolean appliesTo(final MappingContext mappingContext) {
        return true;
    }

    @Override
    public void apply(final MappingContext context) {
        // do nothing

    }

}
