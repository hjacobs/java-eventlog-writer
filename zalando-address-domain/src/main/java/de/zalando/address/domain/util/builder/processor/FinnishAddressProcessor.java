package de.zalando.address.domain.util.builder.processor;

import static de.zalando.address.domain.util.builder.AddressProcessorUtil.compilePatterns;
import static de.zalando.address.domain.util.builder.AddressProcessorUtil.compileReplacePattern;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import de.zalando.address.domain.util.builder.NumberPosition;

import de.zalando.utils.Pair;

public class FinnishAddressProcessor extends AbstractAddressProcessor {

    private static final String APARTMENT_IDENTIFIER_REGEXP = "(?:(?i:bst|as|bostad)\\.?)";

    // matches house number (+ apartment, etc.) cases, like 2 3, 2-3 bst 5, 3/4a A 5, etc.
    private static final String HOUSE_NUMBER =
        "(?:\\b|\\s)(?:(?:\\d+[a-z]?(?:\\s?[-/]\\s?\\d+\\s?)?(?i:[a-z]+)?)(?:\\s(?:[A-Z])(?=\\b))?" + "(?:\\s"
            + APARTMENT_IDENTIFIER_REGEXP + ")?(?:\\s\\d+[a-zA-Z]*)?" + ")";

    //J-
    private static final String[] NUMBER_REGEXP = {
        "(?<=[a-z]+)(?:katu|k\\.?|g\\.?|kuja|kyl[ä|a]|kl\\.?|kj\\.?|gatan?|gr[äa]nd(?:en)?)(?=\\b|\\s)(" + HOUSE_NUMBER + ")",
        "(" + HOUSE_NUMBER + ")"
    };

    private static final String[][] HOUSE_NUMBER_REPLACE_REGEXP = {
        {"(\\s" +APARTMENT_IDENTIFIER_REGEXP + "\\s)", " as. "}
    };

    // additional address parts, like c/o Mika Häkkinen
    public static final String[] ADDITIONAL_REGEXP = {
        "(((?i:\\s?c\\s?/\\s?o\\s+)(?:[^,]+))(?:,))", // c/o Mika Häkkinen
        HOUSE_NUMBER + "(.*)$"
    };

    public static final String[][] ADDITIONAL_REPLACE_REGEXP = {
        {"^(?i:c\\s?/\\s?o)", "c/o"},
        {"[\\p{Punct}\\s]+$", ""},
        {"^[\\p{Punct}\\s]+", ""},
    };
    //J+

    public static final List<Pattern> NUMBER_PATTERNS = compilePatterns(NUMBER_REGEXP);
    public static final List<Pair<Pattern, String>> NUMBER_REPLACE_PATTERNS = compileReplacePattern(
            HOUSE_NUMBER_REPLACE_REGEXP);
    public static final List<Pattern> ADDITIONAL_PATTERNS = compilePatterns(ADDITIONAL_REGEXP);
    public static final List<Pair<Pattern, String>> CITY_REPLACE_PATTERNS = Collections.emptyList();
    public static final List<Pair<Pattern, String>> COMPLETE_STREET_NAME_REPLACE_PATTERNS = Collections.emptyList();
    public static final List<Pair<Pattern, String>> STREET_NAME_REPLACE_PATTERNS = Collections.emptyList();
    public static final List<Pair<Pattern, String>> ADDITIONAL_REPLACE_PATTERNS = compileReplacePattern(
            ADDITIONAL_REPLACE_REGEXP);

    @Override
    protected List<Pair<Pattern, String>> getNumberReplacePatterns() {
        return NUMBER_REPLACE_PATTERNS;
    }

    @Override
    protected List<Pair<Pattern, String>> getStreetNameReplacePatterns() {
        return STREET_NAME_REPLACE_PATTERNS;
    }

    @Override
    protected List<Pair<Pattern, String>> getCompleteStreetNameReplacePatterns() {
        return COMPLETE_STREET_NAME_REPLACE_PATTERNS;
    }

    @Override
    protected List<Pair<Pattern, String>> getCityReplacePatterns() {
        return CITY_REPLACE_PATTERNS;
    }

    @Override
    protected List<Pattern> getNumberPatterns() {
        return NUMBER_PATTERNS;
    }

    @Override
    protected String postProcessHouseNumber(final String houseNumber) {
        return houseNumber.replaceAll("\\s" + APARTMENT_IDENTIFIER_REGEXP + "\\s", " as. ");
    }

    @Override
    protected List<Pattern> getAdditionalPatterns() {
        return ADDITIONAL_PATTERNS;
    }

    @Override
    protected List<Pair<Pattern, String>> getAdditionalReplacePatterns() {
        return ADDITIONAL_REPLACE_PATTERNS;
    }

    @Override
    public NumberPosition getNumberPosition() {
        return NumberPosition.RIGHT;
    }
}
