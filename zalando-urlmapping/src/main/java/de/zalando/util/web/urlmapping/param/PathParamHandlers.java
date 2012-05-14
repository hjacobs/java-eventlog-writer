package de.zalando.util.web.urlmapping.param;

import java.util.Map;

import com.google.common.base.Objects;
import com.google.common.base.Strings;

import de.zalando.util.web.urlmapping.builder.UrlBuilder;

public final class PathParamHandlers {
    private static final class ParameterMapper implements PathParamHandler {

        private static final long serialVersionUID = -4093294580018349926L;
        private final String parameterName;

        private ParameterMapper(final String parameterName) {
            this.parameterName = parameterName;
        }

        @Override
        public boolean appliesTo(final String segment) {
            return true;
        }

        @Override
        public void apply(final String segment, final UrlBuilder urlBuilder,
                final Map<String, String> parameterRegistrationMap) {
            urlBuilder.addParam(parameterName, segment);
            parameterRegistrationMap.put(parameterName, segment);
        }

        @Override
        public boolean equals(final Object obj) {
            return (obj instanceof PathParamHandlers.ParameterMapper)
                ? Objects.equal(parameterName, ((PathParamHandlers.ParameterMapper) obj).parameterName) : false;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(parameterName);
        }

        @Override
        public String toString() {
            return Objects.toStringHelper(this).add("parameterName", parameterName).toString();
        }
    }

    private static final class PrefixDecorator implements PathParamHandler {

        private static final long serialVersionUID = -4805429808654118928L;

        private final PathParamHandler inner;

        private final String prefix;

        private PrefixDecorator(final PathParamHandler inner, final String prefix) {
            this.inner = inner;
            this.prefix = prefix;
        }

        @Override
        public boolean appliesTo(final String segment) {
            return segment.startsWith(prefix) && inner.appliesTo(removeSuffix(segment));
        }

        @Override
        public void apply(final String segment, final UrlBuilder urlBuilder,
                final Map<String, String> parameterRegistrationMap) {
            final String paramValue = removeSuffix(segment);
            inner.apply(paramValue, urlBuilder, parameterRegistrationMap);
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof PathParamHandlers.PrefixDecorator) {
                final PathParamHandlers.PrefixDecorator other = (PathParamHandlers.PrefixDecorator) obj;
                return Objects.equal(prefix, other.prefix) && Objects.equal(inner, other.inner);
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(prefix, inner);
        }

        @Override
        public String toString() {
            return Objects.toStringHelper(this).add("prefix", prefix).add("inner", inner).toString();
        }

        private String removeSuffix(final String segment) {
            return segment.substring(prefix.length());
        }
    }

    private static final class SuffixDecorator implements PathParamHandler {

        private static final long serialVersionUID = 593901115734344136L;
        private final PathParamHandler inner;

        private final String suffix;

        private SuffixDecorator(final PathParamHandler inner, final String suffix) {
            this.inner = inner;
            this.suffix = suffix;
        }

        @Override
        public boolean appliesTo(final String segment) {
            return segment.endsWith(suffix) && inner.appliesTo(removeSuffix(segment));
        }

        @Override
        public void apply(final String segment, final UrlBuilder urlBuilder,
                final Map<String, String> parameterRegistrationMap) {
            final String paramValue = removeSuffix(segment);
            inner.apply(paramValue, urlBuilder, parameterRegistrationMap);
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof PathParamHandlers.SuffixDecorator) {
                final PathParamHandlers.SuffixDecorator other = (PathParamHandlers.SuffixDecorator) obj;
                return Objects.equal(suffix, other.suffix) && Objects.equal(inner, other.inner);
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(suffix, inner);
        }

        @Override
        public String toString() {
            return Objects.toStringHelper(this).add("suffix", suffix).add("inner", inner).toString();
        }

        private String removeSuffix(final String segment) {
            return segment.substring(0, segment.length() - suffix.length());
        }
    }

    enum StandardPathParamHandler implements PathParamHandler {
        MATCH_ANYTHING {
            @Override
            public boolean appliesTo(final String segment) {
                return true;
            }

            @Override
            public void apply(final String segment, final UrlBuilder urlBuilder,
                    final Map<String, String> parameterRegistrationMap) { }

            @Override
            public String toString() {
                return "PathParamHandlers.matchAnything()";
            }
        }

    }

    /**
     * Return a {@link PathParamHandler} that maps any incoming segment to the specified request parameter name.
     */
    public static PathParamHandler mapToParameter(final String parameterName) {
        return new ParameterMapper(parameterName);
    }

    /**
     * Return a {@link PathParamHandler} that matches any incoming segment but doesn't do anything to the mapped
     * request. This is usually used for fixed path segments like /benutzerkonto/.
     */
    public static PathParamHandler matchAnything() {
        return StandardPathParamHandler.MATCH_ANYTHING;
    }

    /**
     * Returns a decorator that only accepts path segments if they begin with a known prefix and strips the prefix from
     * the segment before delegating to the wrapped {@link PathParamHandler}. E.g. if the prefix is "color-", only
     * segments that start with "color-" will be matched and the value "color-blue" will be stripped to "blue" before
     * being passed to the inner handler.
     */
    public static PathParamHandler requirePrefix(final String prefix, final PathParamHandler inner) {
        return new PrefixDecorator(inner, prefix);
    }

    /**
     * Returns a decorator that only accepts path segments if they end with a known suffix and strips the suffix from
     * the segment before delegating to the wrapped {@link PathParamHandler}. E.g. if the suffix is ".txt", only
     * segments that end with ".txt" will be matched and the value "readme.txt" will be stripped to "readme" before
     * being passed to the inner handler.
     */
    public static PathParamHandler requireSuffix(final String suffix, final PathParamHandler inner) {
        return new SuffixDecorator(inner, suffix);
    }

    private PathParamHandlers() { }

    enum SeoParameter implements PathParamHandler {
        OPTIONAL {
            @Override
            public boolean appliesTo(final String segment) {
                return true;
            }
        },
        REQUIRED {

            @Override
            public boolean appliesTo(final String segment) {
                return !Strings.isNullOrEmpty(segment);
            }
        };

        @Override
        public void apply(final String segment, final UrlBuilder urlBuilder,
                final Map<String, String> parameterRegistrationMap) {
            // no-op
        }
    }

    /**
     * Add a handler for a throw-away path segment.
     *
     * @param  optional  if true, the path segment will be optional
     */
    public static Handler addSeoParameter(final boolean optional) {
        if (optional) {
            return SeoParameter.OPTIONAL;
        } else {
            return SeoParameter.REQUIRED;
        }
    }

}
