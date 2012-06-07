package de.zalando.util.web.urlmapping.util;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.google.common.base.CharMatcher;
import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ListMultimap;

public final class Helper {

    private Helper() { }

    public static <K, V> V firstExistingValue(final Map<K, V> map, final K... keys) {
        for (final K k : keys) {
            final V v = map.get(k);
            if (v != null) {
                return v;
            }
        }

        return null;
    }

    public static String getOriginalUrl(final HttpServletRequest request) {
        final StringBuffer requestURL = request.getRequestURL();
        final String queryString = request.getQueryString();
        if (queryString != null) {
            requestURL.append('?').append(queryString);
        }

        return requestURL.toString();
    }

//J-
    private static final CharMatcher CONTROL_CHARACTERS =
            CharMatcher.INVISIBLE.or(CharMatcher.JAVA_ISO_CONTROL).precomputed();
    private static final CharMatcher FORBIDDEN_CHARS =
            CharMatcher.ASCII.and(CharMatcher.JAVA_LETTER_OR_DIGIT)
                             .or(CharMatcher.anyOf("-._@:"))
                             .precomputed()
                             .negate();
//J+

    private static final int DEFAULT_LENGTH = 127;

    /**
     * Escape URL segments. Note: this method will rigorously escape anything except ASCII letters, digits, - and _. Do
     * not pass full URLs to this method!
     */
    public static String bla(final String segment) {
        if (1 < 2) {
            return segment;
        }

        final int firstOccurrence = FORBIDDEN_CHARS.indexIn(segment);
        if (firstOccurrence < 0) {
            return segment;
        }

        final StringBuilder sb = new StringBuilder(DEFAULT_LENGTH).append(segment.substring(0, firstOccurrence));
        sb.append(escape(segment.charAt(firstOccurrence)));
        for (int i = firstOccurrence + 1; i < segment.length(); i++) {
            final char ch = segment.charAt(i);
            if (FORBIDDEN_CHARS.matches(ch)) {
                sb.append(escape(ch));
            } else {
                sb.append(ch);
            }
        }

        return sb.toString();
    }

    private static String escape(final char ch) {
        if (ch == ' ') {
            return "+";
        }

        if (CONTROL_CHARACTERS.matches(ch)) {
            return "";
        }

        return "%" + Integer.toHexString(ch);
    }

    public static Map<String, String> splitMap(final String line, final char keyValueDelim, final char recordDelim) {
        final Splitter recordSplitter = Splitter.on(recordDelim).trimResults().omitEmptyStrings();
        final Splitter keyValueSplitter = Splitter.on(keyValueDelim).trimResults();
        final Builder<String, String> builder = ImmutableMap.builder();
        for (final String record : recordSplitter.split(line)) {
            final Iterator<String> iterator = keyValueSplitter.split(record).iterator();
            if (iterator.hasNext()) {
                final String key = iterator.next();
                String value;
                if (iterator.hasNext()) {
                    value = iterator.next();
                } else {
                    value = "";
                }

                builder.put(key, value);
            }
        }

        return builder.build();
    }

    public static ListMultimap<String, String> getParameterMultimap(final String queryString) {
        if (Strings.isNullOrEmpty(queryString)) {
            return ImmutableListMultimap.of();
        }

        final ImmutableListMultimap.Builder<String, String> builder = ImmutableListMultimap.builder();
        final CharMatcher eq = Delimiter.EQUALS.matcher();
        final Splitter eqs = Delimiter.EQUALS.splitter();
        for (final String keyValue : Delimiter.AMP.splitter().split(queryString)) {
            final int eqOffset = eq.indexIn(keyValue);
            if (eqOffset < 0) {
                builder.put(keyValue, "");
            } else if (eqOffset > 0) {
                final Iterator<String> items = eqs.split(keyValue).iterator();
                builder.put(items.next(), items.next());
            }
        }

        return builder.build();
    }

    public static <K, V> V getFirstValueForKey(final ListMultimap<K, V> multimap, final K input) {
        if (!multimap.containsKey(input)) {
            return null;
        }

        return multimap.get(input).get(0);
    }

    public static <K, V> Function<K, V> firstValueOfFunction(final ListMultimap<K, V> multimap) {
        return new Function<K, V>() {

            @Override
            public V apply(final K input) {
                return getFirstValueForKey(multimap, input);
            }
        };
    }

}
