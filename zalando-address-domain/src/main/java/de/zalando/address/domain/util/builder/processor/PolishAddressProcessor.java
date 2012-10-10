package de.zalando.address.domain.util.builder.processor;

import static de.zalando.address.domain.util.builder.AddressProcessorUtil.compileCasingPatterns;
import static de.zalando.address.domain.util.builder.AddressProcessorUtil.compilePatterns;
import static de.zalando.address.domain.util.builder.AddressProcessorUtil.compileReplacePattern;
import static de.zalando.address.domain.util.builder.AddressProcessorUtil.trimAndCollapse;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableList;

import de.zalando.address.domain.util.builder.AddressProcessorUtil;
import de.zalando.address.domain.util.builder.Casing;
import de.zalando.address.domain.util.builder.NumberPosition;

import de.zalando.utils.Pair;

public class PolishAddressProcessor extends AbstractAddressProcessor {

    // This is what we need to collect.
    private static final String NR_REGEX = "((?:\\d+[\\p{Punct}\\s]+)*\\d+[\\p{Punct}\\s]*[a-zA-Z]*\\b)";

    //J-
    private static final String[] NUMBER_REGEXEN = new String[] {
        "^\\s*(?:(?:(?:ks|ul|os|pl|al|b|ksiądz|ulica|osiedle|plac|alej|biskupa)(?:\\s*\\.)?\\s)*\\d+)\\b[^\\d]+" + NR_REGEX, // for streets containing number in the beginning of their names
        "^(?:[^\\d]+\\d{1,2}\\b)\\s+([0-9]{1,5}(?:\\s*[a-zA-Z]{0,1}(?:\\s+(?i:Mieszkanie|m(?:\\s*\\.))\\s+|\\s*(?:/)\\s*)[0-9]{1,5})?)", // for streets containing number in the tail of their names
        "([0-9]{1,5}\\s*[a-zA-Z]{0,1}(?:\\s+(?:mieszkanie|m)\\s*\\.?\\s+|\\s*(?:/)\\s*)[0-9]{1,5})",
        NR_REGEX + "\\s?[.,]?\\s?[\\p{L}.]+",
        NR_REGEX + "\\s?\\d+\\s?[.,]+",
        NR_REGEX + "^[a-zA-Z]\\s?\\d\\s?(?:\\D\\s?)?", NR_REGEX + "\\d\\s?[,.]+\\s?",
        NR_REGEX
    };

    private static final ImmutableList<Pattern> ADDITIONAL_PATTERNS;

    private static final String[] LOWER_CASE_PATTERNS = new String[] {
        "^ul\\b", "^pl\\b", "^al\\b", // "\\bksiądz\\b", "\\bosiedle\\b", "\\bbiskupa\\b",
        "\\bM\\.\\s"
    };

    private static final String[] ADDITIONAL_REGEXEN = new String[] {};

    private static final String[][] NUMBER_REPLACE_REGEXEN = new String[][] {
        {"\\bM\\b", "m"},
        {"(?<=\\d|\\d[a-z])\\ (?i:mieszkanie|m)\\s*\\.*\\s*(?=\\d)", "/"}, // apartment number is always separated from house number with /
    };

    private static final String[][] STREET_REPLACE_REGEXEN = new String[][] {
        {"\\bks\\b\\.?", "ksiądz"},
        {"\\bulica\\b", "ul."},
        {"\\bplac\\b\\.?", "pl."},
        {"\\balej[ae]\\b\\.?", "al."},
        {"\\bos\\b\\.?", "osiedle"},
        {"\\bbpa\\b\\.?", "biskupa"},
    };

    private static final String[][] COMPLETE_STREET_REPLACE_REGEXEN = new String[][] {};
    private static final String[][] CITY_REPLACE_REGEXEN = new String[0][];

    private static final ImmutableList<Pattern> NUMBER_PATTERNS;
    private static final ImmutableList<Pair<Pattern, Casing>> STREET_CASE_PATTERNS;
    private static final ImmutableList<Pair<Pattern, String>> NUMBER_REPLACE_PATTERNS;
    private static final ImmutableList<Pair<Pattern, String>> STREET_REPLACE_PATTERNS;

    private static final ImmutableList<Pair<Pattern, String>> COMPLETE_STREET_REPLACE_PATTERNS;
    private static final ImmutableList<Pair<Pattern, String>> CITY_REPLACE_PATTERNS;

    private static final String[][] ZIP_FORMAT_REGEXP = {
        {"^(\\d{2})(\\d{3})", "$1-$2"}
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
        STREET_CASE_PATTERNS = compileCasingPatterns(LOWER_CASE_PATTERNS, null, null, Pattern.CASE_INSENSITIVE);

        // we use ZIP_FORMAT_PATTERN only locally and as a single replace pattern, thus get (0);
        ZIP_FORMAT_PATTERN = compileReplacePattern(ZIP_FORMAT_REGEXP, Pattern.CASE_INSENSITIVE).get(0);

    }
    //J+

    @Override
    public NumberPosition getNumberPosition() {
        return NumberPosition.RIGHT;
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
    protected List<Pair<Pattern, Casing>> getStreetCasePatterns() {
        return STREET_CASE_PATTERNS;
    }

    @Override
    public String normalizeHouseNumber(final String houseNumber) {

        String r = super.normalizeHouseNumber(houseNumber);
        return fixCase(r);

    }
}
