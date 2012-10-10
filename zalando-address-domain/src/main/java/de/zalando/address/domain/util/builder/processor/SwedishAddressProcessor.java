package de.zalando.address.domain.util.builder.processor;

import static de.zalando.address.domain.util.builder.AddressProcessorUtil.compilePatterns;
import static de.zalando.address.domain.util.builder.AddressProcessorUtil.compileReplacePattern;
import static de.zalando.address.domain.util.builder.AddressProcessorUtil.trimAndCollapse;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableList;

import de.zalando.address.domain.util.builder.AddressProcessorUtil;
import de.zalando.address.domain.util.builder.NumberPosition;

import de.zalando.utils.Pair;

public class SwedishAddressProcessor extends AbstractAddressProcessor {

    // This is what we need to collect.
    private static final String NR_REGEX = "((?:\\d+[\\p{Punct}\\s]+)*\\d+[\\p{Punct}\\s]*[a-zA-Z]*\\b)";

    private static final String[] NUMBER_REGEXEN = new String[] {
        "([0-9]{1,5}\\s+(?:[0-9]{1,5}\\s*)*(?:\\b[a-zA-Z]\\b)?)",
        "([0-9]{1,5}\\s+(?:[0-9]{1,5}\\s*)*(?:\\b[a-zA-Z]\\b)?)", NR_REGEX + "\\s?[.,]?\\s?[\\p{L}.]+",
        NR_REGEX + "\\s?\\d+\\s?[.,]+", NR_REGEX + "^[a-zA-Z]\\s?\\d\\s?(?:\\D\\s?)?", NR_REGEX + "\\d\\s?[,.]+\\s?",
        NR_REGEX, "([0-9]{1,5} ?[a-zA-Z]{0,1})"
    };

    private static final ImmutableList<Pattern> ADDITIONAL_PATTERNS;

    private static final String[] ADDITIONAL_REGEXEN = new String[] {"((?:\\b(?:BV|TR|TR\\s+N|ÖG)\\b\\s*)+)"};
    private static final String[][] NUMBER_REPLACE_REGEXEN = new String[][] {};
    private static final String[][] STREET_REPLACE_REGEXEN = new String[][] {};

    private static final String[][] COMPLETE_STREET_REPLACE_REGEXEN = new String[][] {};
    private static final String[][] CITY_REPLACE_REGEXEN = new String[0][];

    private static final ImmutableList<Pattern> NUMBER_PATTERNS;
    private static final ImmutableList<Pair<Pattern, String>> NUMBER_REPLACE_PATTERNS;
    private static final ImmutableList<Pair<Pattern, String>> STREET_REPLACE_PATTERNS;

    private static final ImmutableList<Pair<Pattern, String>> COMPLETE_STREET_REPLACE_PATTERNS;
    private static final ImmutableList<Pair<Pattern, String>> CITY_REPLACE_PATTERNS;

    private static final String[][] ZIP_FORMAT_REGEXP = {
        {"^(\\d{3})(\\d{2})", "$1 $2"}
    };
    private static final Pair<Pattern, String> ZIP_FORMAT_PATTERN;

    private static final Pattern NOT_ALLOWED_CHARS_ZIP = Pattern.compile("[^\\d]");

    static {
        NUMBER_PATTERNS = compilePatterns(NUMBER_REGEXEN, Pattern.CASE_INSENSITIVE);
        ADDITIONAL_PATTERNS = compilePatterns(ADDITIONAL_REGEXEN, Pattern.CASE_INSENSITIVE);
        NUMBER_REPLACE_PATTERNS = compileReplacePattern(NUMBER_REPLACE_REGEXEN);
        STREET_REPLACE_PATTERNS = compileReplacePattern(STREET_REPLACE_REGEXEN, Pattern.CASE_INSENSITIVE);

        COMPLETE_STREET_REPLACE_PATTERNS = compileReplacePattern(COMPLETE_STREET_REPLACE_REGEXEN,
                Pattern.CASE_INSENSITIVE);
        CITY_REPLACE_PATTERNS = compileReplacePattern(CITY_REPLACE_REGEXEN, Pattern.CASE_INSENSITIVE);

        // we use ZIP_FORMAT_PATTERN only locally and as a single replace pattern, thus get (0);
        ZIP_FORMAT_PATTERN = compileReplacePattern(ZIP_FORMAT_REGEXP, Pattern.CASE_INSENSITIVE).get(0);
    }

    @Override
    public String normalizeZip(final String zip) {

        String result = AddressProcessorUtil.replaceBackSlashesWithSlashes(zip);

        Matcher matcher = NOT_ALLOWED_CHARS_ZIP.matcher(result);
        result = matcher.replaceAll("");
        result = trimAndCollapse(result);

        matcher = ZIP_FORMAT_PATTERN.getFirst().matcher(result);
        result = matcher.replaceAll(ZIP_FORMAT_PATTERN.getSecond());

        return result;
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

}
