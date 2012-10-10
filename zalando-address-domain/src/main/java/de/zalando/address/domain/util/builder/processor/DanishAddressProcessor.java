package de.zalando.address.domain.util.builder.processor;

import static de.zalando.address.domain.util.builder.AddressProcessorUtil.compilePatterns;
import static de.zalando.address.domain.util.builder.AddressProcessorUtil.compileReplacePattern;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableList;

import de.zalando.address.domain.util.builder.NumberPosition;

import de.zalando.utils.Pair;

public class DanishAddressProcessor extends AbstractAddressProcessor {

    //J-

    // This is what we need to collect.
    public static final String NR_BASE_REGEX = "(?:(?:\\d+[\\p{Punct}\\s]+)*\\d+[\\p{Punct}\\s]*[a-zA-Z]*)";

    private static final String NR_REGEX = "(" + NR_BASE_REGEX + "\\b)";

    public static final String DIRECTION_LITERAL_REGEXP = "(?:\\.?(?:tv|th|til h[øo]jre|til venstre))";

    private static final String[] NUMBER_REGEXEN = new String[] {
        "(" + NR_REGEX + "(?:,?\\s?|\\s*)" + NR_BASE_REGEX + DIRECTION_LITERAL_REGEXP + "?)",
        "\\d+\\s?[\\.,]?\\s?[\\p{L}.]+\\s" + NR_REGEX,
        "\\d+\\s?[\\.,]+\\s?" + NR_REGEX,
        "^[a-zA-Z]\\s?\\d\\s?(?:\\D\\s?)?" + NR_REGEX,
        "\\d\\s?[,\\.]+\\s?" + NR_REGEX,
        NR_REGEX,
        "([0-9]{1,5} ?[a-zA-Z]{0,1})"
    };

    private static final String[] ADDITIONAL_REGEXEN = {
        "(?!" + NR_BASE_REGEX + "),\\s(?!" + NR_BASE_REGEX + ")([^.]*$)"
    };

    private static final String[][] NUMBER_REPLACE_REGEXEN = {
        {"\\b0+\\b", ""},
        {"[^-/\\d\\p{L}.]+", " "},
        {"\\b0+(.*)", "$1"},
        {" ?/+ ?", "/"},
        {" ?-+ ?", "-"},
        {" ?\\.+ ?", "."},
        {"[-/.]{2,}", "/"},
        {"^[-/.]+(\\d.*)", "$1"},
        {"(\\d)[-/ ]+([a-zA-Z]+)", "$1$2"},
        {"(.*\\d)[-/.]+$", "$1"}
    };

    private static final String[][] STREET_REPLACE_REGEXEN = {
        // {"\\b(\\d+)((?:tv|th))\\b", "$1 $2"},
        {"\\b[Øø]\\b", "Øst"},
//        {"(\\d+)\\s*\\.?\\s*\\b?th\\b", "$1 til højre"},
//        {"(\\d+)\\s*\\.?\\s*\\b?tv\\b", "$1 til venstre"}
    };

    private static final String[][] COMPLETE_STREET_REPLACE_REGEXEN = {

    };

    private static final String[][] CITY_REPLACE_REGEXEN = {
        {"\\b[Øø]\\b", "Øst"},
        {"\\b[Cc]\\b", "City"}
    };
    //J+

    private static final ImmutableList<Pattern> NUMBER_PATTERNS;
    private static final ImmutableList<Pair<Pattern, String>> NUMBER_REPLACE_PATTERNS;
    private static final ImmutableList<Pair<Pattern, String>> STREET_REPLACE_PATTERNS;

    private static final ImmutableList<Pair<Pattern, String>> COMPLETE_STREET_REPLACE_PATTERNS;
    private static final ImmutableList<Pair<Pattern, String>> CITY_REPLACE_PATTERNS;

    private static final ImmutableList<Pattern> ADDITIONAL_PATTERNS;

    // captures 3[.]< th | tv >
    // th (til højre) - means 'on the right'
    // tv (til venstre) - means 'on the left'
    public static final Pattern RIGHT_LEFT_LITERAL_PATTERN = Pattern.compile("(([\\da-z])\\.?((?i:th|tv)))$",
            Pattern.CASE_INSENSITIVE);

    static {
        NUMBER_PATTERNS = compilePatterns(NUMBER_REGEXEN, Pattern.CASE_INSENSITIVE);
        ADDITIONAL_PATTERNS = compilePatterns(ADDITIONAL_REGEXEN, Pattern.CASE_INSENSITIVE);
        NUMBER_REPLACE_PATTERNS = compileReplacePattern(NUMBER_REPLACE_REGEXEN);
        STREET_REPLACE_PATTERNS = compileReplacePattern(STREET_REPLACE_REGEXEN, Pattern.CASE_INSENSITIVE);

        COMPLETE_STREET_REPLACE_PATTERNS = compileReplacePattern(COMPLETE_STREET_REPLACE_REGEXEN,
                Pattern.CASE_INSENSITIVE);
        CITY_REPLACE_PATTERNS = compileReplacePattern(CITY_REPLACE_REGEXEN, Pattern.CASE_INSENSITIVE);
    }

    @Override
    public NumberPosition getNumberPosition() {
        return NumberPosition.RIGHT;
    }

    @Override
    protected List<Pair<Pattern, String>> getNumberReplacePatterns() {
        return NUMBER_REPLACE_PATTERNS;
    }

    @Override
    protected List<Pattern> getNumberPatterns() {
        return NUMBER_PATTERNS;
    }

    @Override
    protected List<Pattern> getAdditionalPatterns() {
        return ADDITIONAL_PATTERNS;
    }

    @Override
    protected List<Pair<Pattern, String>> getStreetNameReplacePatterns() {
        return STREET_REPLACE_PATTERNS;
    }

    @Override
    protected List<Pair<Pattern, String>> getCompleteStreetNameReplacePatterns() {
        return COMPLETE_STREET_REPLACE_PATTERNS;
    }

    @Override
    protected List<Pair<Pattern, String>> getCityReplacePatterns() {
        return CITY_REPLACE_PATTERNS;
    }

    @Override
    protected String postProcessHouseNumber(final String houseNumber) {
        final StringBuffer sb = new StringBuffer();

        final Matcher m = RIGHT_LEFT_LITERAL_PATTERN.matcher(houseNumber);
        while (m.find()) {
            m.appendReplacement(sb, String.format("%s.%s", m.group(2), m.group(3).toLowerCase()));
        }

        m.appendTail(sb);

        return sb.toString();
    }

}
