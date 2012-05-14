package de.zalando.util.web.urlmapping.builder;

import static com.google.common.base.Preconditions.checkArgument;

import de.zalando.util.web.urlmapping.domain.MappingConstants;

public class PathBuilder {

    private boolean wildCardPresent;
    private boolean optionalPresent;

    public PathBuilder add(final String segment) {
        checkArgument(!wildCardPresent, "Fixed path segment not allowed after wildcard");
        checkArgument(MappingConstants.ALLOWED_CHARS.matchesAllOf(segment), "Segment '%s' contains illegal characters",
            segment);
        addSlashIfNecessary().append(segment);
        return this;
    }

    private StringBuilder addSlashIfNecessary() {
        if (builder.length() > 0) {
            builder.append('/');
        }

        return builder;
    }

    public PathBuilder addWildCard() {
        checkArgument(!optionalPresent, "Non-optional wild card not allowed after optional wildcard");
        addSlashIfNecessary().append(MappingConstants.WILDCARD);
        wildCardPresent = true;
        return this;
    }

    public PathBuilder addOptionalWildCard() {
        addSlashIfNecessary().append(MappingConstants.OPTIONAL_WILDCARD);
        optionalPresent = true;
        wildCardPresent = true;
        return this;
    }

    private final StringBuilder builder = new StringBuilder();

    public String build() {
        return builder.toString();
    }

}
