package de.zalando.util.web.urlmapping.builder;

import static java.util.Collections.emptyMap;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Predicates.in;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newArrayListWithExpectedSize;
import static com.google.common.collect.Maps.filterKeys;

import static de.zalando.util.web.urlmapping.util.Helper.urlEncodeSegment;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import de.zalando.util.web.urlmapping.util.Delimiter;

public class UrlBuilder {
    private static final int DEFAULT_CAPACITY = 255;
    private static final int DEFAULT_LIST_CAPACITY = 5;

    private static void addEntry(final StringBuilder sb, final boolean hasQuery, final String key, final String value) {
//J-
        final char delim;
        if (hasQuery) { delim = '&'; } else { delim = '?'; }
        sb.append(delim)
          .append(urlEncodeSegment(key))
          .append('=')
          .append(urlEncodeSegment(value));
//J+
    }

    private final String baseUrl;
    private Map<String, String> firstParameter = ImmutableMap.of();
    private Multimap<String, String> params = ImmutableMultimap.of();
    private List<String> pathSegments = ImmutableList.of();
    private List<String> paramsToRemove = ImmutableList.of();
    private Map<String, String[]> remainingParams = emptyMap();

    public UrlBuilder(final String baseUrl) {
        String myBaseUrl = Delimiter.SLASH.matcher().trimTrailingFrom(baseUrl);
        if (myBaseUrl.isEmpty() || (myBaseUrl.charAt(0) != '/')) {
            myBaseUrl = '/' + myBaseUrl;
        }

        this.baseUrl = myBaseUrl;
    }

    public UrlBuilder setFirstParameter(final String key, final String value) {
        checkArgument(firstParameter.isEmpty(), "First parameter can only be set once!");
        this.firstParameter = ImmutableMap.of(key, value);
        return this;
    }

    public UrlBuilder addParam(final String key, final String value) {
        if (params.isEmpty()) {
            params = LinkedListMultimap.create();
        }

        params.put(key, value);
        return this;
    }

    public UrlBuilder addPathSegment(final String segment) {
        if (pathSegments.isEmpty()) {
            pathSegments = newArrayListWithExpectedSize(DEFAULT_LIST_CAPACITY);
        }

        pathSegments.add(segment);

        return this;
    }

    public String build() {
        final StringBuilder sb = new StringBuilder(DEFAULT_CAPACITY).append(baseUrl);

        for (final String segment : pathSegments) {
            sb.append('/').append(urlEncodeSegment(segment));
        }

        boolean hasQuery = Delimiter.QUESTION.matcher().matchesAnyOf(sb);

        if (!firstParameter.isEmpty()) {
            for (final Entry<String, String> entry : firstParameter.entrySet()) {
                addEntry(sb, hasQuery, entry.getKey(), entry.getValue());
                hasQuery = true;
            }
        }

        if (!params.isEmpty()) {
            for (final Entry<String, String> entry : params.entries()) {
                addEntry(sb, hasQuery, entry.getKey(), entry.getValue());
                hasQuery = true;
            }
        }

        if (!remainingParams.isEmpty()) {
            for (final Entry<String, String[]> entry : remainingParams.entrySet()) {
                final String key = entry.getKey();
                for (final String value : entry.getValue()) {
                    addEntry(sb, hasQuery, key, value);
                    hasQuery = true;
                }
            }
        }

        return sb.toString();
    }

    /**
     * Store original request parameters in a map, but without the parameters we already used or have explicitly vetoed.
     */
    public UrlBuilder takeRemainingParametersFromOriginalMapping(final Map<String, String[]> parameterMap) {
        if (!parameterMap.isEmpty()) {
            Map<String, String[]> incoming = parameterMap;
            if (!firstParameter.isEmpty()) {
                incoming = filterKeys(incoming, not(in(firstParameter.keySet())));
            }

            if (!params.isEmpty()) {
                incoming = filterKeys(incoming, not(in(params.keySet())));
            }

            if (!paramsToRemove.isEmpty()) {
                incoming = filterKeys(incoming, not(in(paramsToRemove)));
            }

            if (!incoming.isEmpty()) {
                remainingParams = ImmutableMap.copyOf(incoming);
            }
        }

        return this;
    }

    /**
     * Veto the use of these request parameters.
     */
    public UrlBuilder removeParams(final Collection<String> incoming) {
        if (!incoming.isEmpty()) {
            if (paramsToRemove.isEmpty()) {
                paramsToRemove = newArrayList();
            }

            paramsToRemove.addAll(incoming);
        }

        return this;
    }

    /**
     * Veto the use of this request parameter.
     */
    public UrlBuilder removeParam(final String paramName) {
        if (paramsToRemove.isEmpty()) {
            paramsToRemove = newArrayList();
        }

        paramsToRemove.add(paramName);

        return this;
    }

}
