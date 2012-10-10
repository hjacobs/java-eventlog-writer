package de.zalando.address.domain.util.builder.processor;

import static de.zalando.address.domain.util.builder.AddressProcessorUtil.compilePatterns;
import static de.zalando.address.domain.util.builder.AddressProcessorUtil.compileReplacePattern;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

import de.zalando.address.domain.util.builder.NumberPosition;

import de.zalando.utils.Pair;

public class BelgianAddressProcessor extends AbstractAddressProcessor {

    // This is what we need to collect.
    private static final String NR_REGEX = "((?:\\d+[\\p{Punct}\\s]+)*\\d+[\\p{Punct}\\s]*[a-zA-Z]{0,1}\\b)";

    //J-
    private static final String[] NUMBER_REGEXEN = new String[] {
        "^.*\\w+(?:straat|laan)\\s+" + NR_REGEX,
        "\\d+\\s?[\\.,]?\\s?[\\p{L}.]+\\s" + NR_REGEX,
        "\\d+\\s?[\\.,]+\\s?" + NR_REGEX,
        "^[a-zA-Z]\\s?\\d\\s?(?:\\D\\s?)?" + NR_REGEX,
        "([0-9]{1,5}\\s*[a-zA-Z]{0,1})",
        NR_REGEX,
    };

    private static final Pattern HOUSE_NUMBER_FIRST_PATTERN = Pattern.compile("^" + NR_REGEX + "(.*)");

    private static final ImmutableList<Pattern> ADDITIONAL_PATTERNS;

    private static final String[] ADDITIONAL_REGEXEN = new String[] {
        ",\\s*(.*$)",
        "(\\bt\\.?a\\.?v\\.?\\s+.+)$",
        "[0-9]+\\s*[a-zA-Z]\\s+(.*)$",
        "[0-9]+\\s*([a-zA-Z]\\w+\\s+.*)$",
        "\\b((?i:T\\s*\\.?\\s*(?:A\\s*\\.\\s*?V\\s*\\.\\s*?)?)\\d+.*)$",
    };
    private static final String[][] NUMBER_REPLACE_REGEXEN = new String[][] {
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
    private static final String[][] STREET_REPLACE_REGEXEN = new String[][] {};

    private static final String[][] COMPLETE_STREET_REPLACE_REGEXEN = new String[][] {};
    private static final String[][] CITY_REPLACE_REGEXEN = new String[][] {};

    private static final ImmutableList<Pattern> NUMBER_PATTERNS;
    private static final ImmutableList<Pair<Pattern, String>> NUMBER_REPLACE_PATTERNS;
    private static final ImmutableList<Pair<Pattern, String>> STREET_REPLACE_PATTERNS;

    private static final ImmutableList<Pair<Pattern, String>> COMPLETE_STREET_REPLACE_PATTERNS;
    private static final ImmutableList<Pair<Pattern, String>> CITY_REPLACE_PATTERNS;
    //J+

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
    public Pair<String, String> normalizeStreet(final String streetWithNumberInput) {

        // if street line starts with house number, preprocess it and put the house number after the street name
        final String streetWithNumber;
        final Matcher m = HOUSE_NUMBER_FIRST_PATTERN.matcher(streetWithNumberInput);
        if (m.matches()) {
            final String houseNumber = m.group(1);
            final String streetName = m.group(2);
            final Pair<String, String> streetAndAdditional = super.normalizeStreet(streetName);
            streetWithNumber = Joiner.on(' ').skipNulls().join(
                    new String[] {streetAndAdditional.getFirst(), houseNumber, streetAndAdditional.getSecond()});
        } else {
            streetWithNumber = streetWithNumberInput;
        }

        return super.normalizeStreet(streetWithNumber);
    }
}
