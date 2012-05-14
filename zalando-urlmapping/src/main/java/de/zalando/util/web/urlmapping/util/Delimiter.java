package de.zalando.util.web.urlmapping.util;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

public enum Delimiter {
    DOT('.'),
    SLASH('/'),
    PIPE('|'),
    COMMA(','),
    WHITESPACE(" \t"),
    NEWLINE('\n'),
    QUESTION('?'),
    EQUALS('='),
    AMP('&'),
    SEMI(';');

    private CharMatcher charMatcher;
    private Joiner joiner;
    private Splitter splitter;
    private Splitter trimmedSplitter;

    private Delimiter(final char delim) {
        this(CharMatcher.is(delim), Joiner.on(delim));
    }

    private Delimiter(final CharMatcher charMatcher, final Joiner joiner) {
        this.charMatcher = charMatcher;
        this.joiner = joiner;
        this.splitter = Splitter.on(charMatcher);
        this.trimmedSplitter = splitter.trimResults().omitEmptyStrings();
    }

    private Delimiter(final String delim) {
        this(CharMatcher.anyOf(delim), Joiner.on(delim.charAt(0)));
    }

    public CharMatcher matcher() {
        return charMatcher;
    }

    public Joiner joiner() {
        return joiner;
    }

    public Splitter splitter() {
        return splitter;
    }

    public Splitter trimmedSplitter() {
        return trimmedSplitter;
    }
}
