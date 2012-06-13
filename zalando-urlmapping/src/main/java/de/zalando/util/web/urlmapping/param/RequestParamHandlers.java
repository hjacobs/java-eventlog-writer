package de.zalando.util.web.urlmapping.param;

import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.asList;

import java.util.List;
import java.util.Map;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;

import de.zalando.util.web.urlmapping.builder.UrlBuilder;
import de.zalando.util.web.urlmapping.util.Helper;

/**
 * Factory methods for acquiring RequestParamHandler objects.
 *
 * @author  Sean Patrick Floyd (sean.floyd@zalando.de)
 */
public final class RequestParamHandlers {

    private static final class AggregationParamHandler implements RequestParamHandler {

        private static final long serialVersionUID = 920936969227182628L;
        private final List<String> incoming;
        private final String paramName;
        private final char delimiter;

        private AggregationParamHandler(final List<String> incoming, final String paramName, final char delimiter) {
            this.incoming = incoming;
            this.paramName = paramName;
            this.delimiter = delimiter;
        }

        @Override
        public boolean appliesTo(final ListMultimap<String, String> parameterMap) {
            return parameterMap.keySet().containsAll(incoming);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void apply(final ListMultimap<String, String> parameterMap, final UrlBuilder urlBuilder,
                final Map<String, String> outgoingMap) {
            final String outgoingValue = Joiner.on(delimiter).join(transform(incoming,
                        Helper.firstValueOfFunction(parameterMap)));
            outgoingMap.put(paramName, outgoingValue);
            urlBuilder.addParam(paramName, outgoingValue);
            urlBuilder.removeParams(incoming);
        }
    }

    private static final class RequireParameterHandler implements RequestParamHandler {

        private static final long serialVersionUID = -5429064179697563079L;

        private final String paramName;

        private RequireParameterHandler(final String paramName) {
            this.paramName = paramName;
        }

        @Override
        public boolean appliesTo(final ListMultimap<String, String> parameterMap) {
            return parameterMap.containsKey(paramName);
        }

        @Override
        public void apply(final ListMultimap<String, String> parameterMap, final UrlBuilder urlBuilder,
                final Map<String, String> outgoingMap) {
            /* noop */
        }

        @Override
        public boolean equals(final Object obj) {
            return (obj instanceof RequestParamHandlers.RequireParameterHandler)
                ? Objects.equal(paramName, ((RequestParamHandlers.RequireParameterHandler) obj).paramName) : false;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(paramName);
        }

        @Override
        public String toString() {
            return Objects.toStringHelper(this).add("paramName", paramName).toString();
        }
    }

    private static final class SimpleParamMapper implements RequestParamHandler {

        private static final long serialVersionUID = 3433836060058304553L;

        private final String outParamName;

        private final String paramName;

        private SimpleParamMapper(final String outParamName, final String paramName) {
            this.outParamName = outParamName;
            this.paramName = paramName;
        }

        @Override
        public boolean appliesTo(final ListMultimap<String, String> parameterMap) {
            return parameterMap.containsKey(paramName);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void apply(final ListMultimap<String, String> parameterMap, final UrlBuilder urlBuilder,
                final Map<String, String> outgoingMap) {
            final String outValue = Helper.getFirstValueForKey(parameterMap, paramName);
            urlBuilder.addParam(outParamName, outValue);
            urlBuilder.removeParam(paramName);
            outgoingMap.put(outParamName, outValue);
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof RequestParamHandlers.SimpleParamMapper) {
                final RequestParamHandlers.SimpleParamMapper other = (RequestParamHandlers.SimpleParamMapper) obj;
                return Objects.equal(paramName, other.paramName) && Objects.equal(outParamName, other.outParamName);
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(paramName, outParamName);
        }

        @Override
        public String toString() {
            return Objects.toStringHelper(this).add("paramName", paramName).add("outParamName", outParamName)
                          .toString();
        }
    }

    /**
     * A {@link RequestParamHandler} that maps a specified incoming request parameter to the equivalent out parameter.
     */
    public static RequestParamHandler mapIncomingParameter(final String paramName, final String outParamName) {
        return new SimpleParamMapper(outParamName, paramName);
    }

    /**
     * A {@link RequestParamHandler} that requires that a parameter of the supplied name be present without actually
     * doing anything with it.
     */
    public static RequestParamHandler requireParam(final String paramName) {
        return new RequireParameterHandler(paramName);
    }

    /**
     * Return a {@link RequestParamHandler} that aggregates multiple incoming parameters into an outgoing parameter with
     * a specified delimiter.
     *
     * @param  paramName  the outgoing parameter
     * @param  delimiter  the delimiter
     */
    public static RequestParamHandler aggregate(final String paramName, final char delimiter, final String first,
            final String second, final String... more) {
        return new AggregationParamHandler(ImmutableList.copyOf(asList(first, second, more)), paramName, delimiter);
    }

    private static String getFirstValue(final Map<String, String[]> parameterMap, final String name) {
        final String[] strings = parameterMap.get(name);
        if ((strings == null) || (strings.length == 0)) {
            return "";
        } else {
            return strings[0];
        }
    }

    private RequestParamHandlers() { }
}
