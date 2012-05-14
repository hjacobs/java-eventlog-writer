package de.zalando.util.web.urlmapping.domain;

import java.util.Set;

import com.google.common.base.CharMatcher;
import com.google.common.collect.ImmutableSet;

public final class MappingConstants {

    public static final String WILDCARD = "*";
    public static final String OPTIONAL_WILDCARD = "?";
    public static final Set<String> ALL_WILDCARDS = ImmutableSet.of("*", "", "?");
//J-
    public static final CharMatcher ALLOWED_CHARS = // url segments may be digits, lowercase letters, periods and dashes
        CharMatcher.inRange('a', 'z')
                   .or(CharMatcher.inRange('A', 'Z'))
                   .or(CharMatcher.inRange('0', '9'))
                   .or(CharMatcher.anyOf("-_."))
                   .precomputed();
//J+
    public static final CharMatcher ALLOWED_PATH_CHARACTERS = ALLOWED_CHARS.or(CharMatcher.is('/')).precomputed();

    private MappingConstants() { }

}
