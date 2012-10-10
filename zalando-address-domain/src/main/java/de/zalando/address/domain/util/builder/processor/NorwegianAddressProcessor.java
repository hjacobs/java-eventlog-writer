package de.zalando.address.domain.util.builder.processor;

import static de.zalando.address.domain.util.builder.AddressProcessorUtil.compilePatterns;
import static de.zalando.address.domain.util.builder.AddressProcessorUtil.compileReplacePattern;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableList;

import de.zalando.address.domain.util.builder.NumberPosition;

import de.zalando.utils.Pair;

public class NorwegianAddressProcessor extends AbstractAddressProcessor {

    // This is what we need to collect.
    private static final String NR_REGEX = "((?:\\d+[\\p{Punct}\\s]+)*\\d+[\\p{Punct}\\s]*[a-zA-Z]*\\b)";

    private static final String POSTBOX_REGEX = "(pb\\s+\\d+\\b)"; // e.g. PB 0182

    private static final String APARTMENT_REGEX = "([HUKL][\\.\\s]*\\d+\\b)"; // e.g. H0101

    //J-
    private static final String[] HOUSE_NUMBER_REGEXP = {
        NR_REGEX
    };

    private static final String[][] COMPLETE_STREET_NAME_REPLACE_REFEXP = {
            {"^\\s*(" + POSTBOX_REGEX + ")\\s*(.*)", "$3 $2"}, // put PB after everything, if it's in front
            {"^\\s*(" + APARTMENT_REGEX + ")\\s*(.*)", "$3 $2"}  // put apartment number after everything, if it's in front
    };

    private static final String[][] STREET_NAME_REPLACE_REGEXP = {
        {"((?<=[^\\d\\s\\p{Punct}])vn\\.?)\\s+", "veien "},
        {"\\bgt(\\s*(?:\\.\\s*)?)?\\b", " Gate "},
        {"\\bpl(\\s*(?:\\.\\s*)?)?\\b", " Plass "},
        {"\\btrg(\\s*(?:\\.\\s*)?)?\\b", " Torget "},
        {"\\bal(\\s*(?:\\.\\s*)?)?\\b", " Allé "}
    };

    private static final String[][] HOUSE_NUMBER_REPLACE_REGEXP = {
        {"(\\d+)([^\\d].*)", "$1 $2"}
    };

    private static final String APARTMENT_IDENTIFIER_BASE_REGEXP = "(?:Leilg?|Lei|Lg|L\\s*\\.)";

    private static final String HOUSE_IDENTIFIER_BASE_REGEXP = "(?:Hus)";

    private static final String FLOOR_IDENTIFIER_BASE_REGEXP = "(?:Etasje|Etage|Etg|Et)";

    private static final String[] ADDITIONAL_REGEXP = {
        "(\\b(?:" + HOUSE_IDENTIFIER_BASE_REGEXP + "|" + APARTMENT_IDENTIFIER_BASE_REGEXP + "|" + FLOOR_IDENTIFIER_BASE_REGEXP + "|oppgang)\\s*[\\p{Punct}]*\\b.*)$",
        "(" + POSTBOX_REGEX + ".*)$",
        "(" + APARTMENT_REGEX + ".*)$",
        ".+,(.+)$",
    };

    private static final String[][] ADDITIONAL_REPLACE_REGEXP = {
        {"\\b" + HOUSE_IDENTIFIER_BASE_REGEXP + "(?:\\s*\\p{Punct})?", "H."},
        {"\\b" + APARTMENT_IDENTIFIER_BASE_REGEXP + "(?:\\s*\\p{Punct})?", "Leil."},
        {"\\b" + FLOOR_IDENTIFIER_BASE_REGEXP + "(?:\\s*\\p{Punct})?", "Et."},
    };

    private static final List<Pattern> HOUSE_NUMBER_PATTERNS = compilePatterns(HOUSE_NUMBER_REGEXP,
            Pattern.CASE_INSENSITIVE);

    private static final ImmutableList<Pair<Pattern,String>> STREET_NAME_REPLACE_PATTERNS = compileReplacePattern(
            STREET_NAME_REPLACE_REGEXP, Pattern.CASE_INSENSITIVE);

    private static final ImmutableList<Pair<Pattern,String>> HOUSE_NUMBER_REPLACE_PATTERNS = compileReplacePattern(
            HOUSE_NUMBER_REPLACE_REGEXP, Pattern.CASE_INSENSITIVE);

    private static final ImmutableList<Pattern> ADDITIONAL_PATTERNS = compilePatterns(ADDITIONAL_REGEXP,
            Pattern.CASE_INSENSITIVE);

    private static final List<Pair<Pattern, String>> COMPLETE_STREET_NAME_REPLACE_PATTERNS = compileReplacePattern(
            COMPLETE_STREET_NAME_REPLACE_REFEXP, Pattern.CASE_INSENSITIVE);

    private static final ImmutableList<Pair<Pattern,String>> ADDITIONAL_REPLACE_PATTERNS = compileReplacePattern(
            ADDITIONAL_REPLACE_REGEXP, Pattern.CASE_INSENSITIVE);

    private static final List<Pair<Pattern,String>> CITY_REPLACE_PATTERNS = Collections.emptyList();
    //J+

    @Override
    protected List<Pair<Pattern, String>> getNumberReplacePatterns() {
        return HOUSE_NUMBER_REPLACE_PATTERNS;
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
        return HOUSE_NUMBER_PATTERNS;
    }

    @Override
    protected List<Pattern> getAdditionalPatterns() {
        return ADDITIONAL_PATTERNS;
    }

    @Override
    public NumberPosition getNumberPosition() {
        return NumberPosition.RIGHT;
    }

    @Override
    protected List<Pair<Pattern, String>> getAdditionalReplacePatterns() {
        return ADDITIONAL_REPLACE_PATTERNS;
    }
}
