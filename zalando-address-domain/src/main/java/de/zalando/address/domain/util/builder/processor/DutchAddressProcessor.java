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

public class DutchAddressProcessor extends AbstractAddressProcessor {

    // This is what we need to collect.
    private static final String NR_REGEX = "((?:\\d+[\\p{Punct}\\s]+)*\\d+[\\p{Punct}\\s]*[a-zA-Z]?\\b)";

    // @formatter:off
    private static final String[] NUMBER_REGEXEN = new String[] {
        "\\w+\\s\\w+\\s\\d+\\s\\w+\\s\\d+(?:[0-9],)?\\s" + NR_REGEX, "\\w+\\s\\d+\\w\\s\\w+\\s" + NR_REGEX,

        // in berlin gibt es eine Straße , die Straße heisst
        "^\\s?[Ss]tr(?:a(?:ß|ss)e)?\\.?\\s\\d+[-.\\s\\/]+" + NR_REGEX,

        // Strassenname gefolgt von Angabe der Wohnung z.B. 1.Og
        "^(?:\\de){0,1}[^0-9]*?" + NR_REGEX + "[.,]*\\s\\d+[.\\s]*[OoGg]",

        // Strasse mit Ordinalzahl 5.Wallstraße
        "\\d+\\s?[.,]?\\s?[\\p{L}.]+\\s" + NR_REGEX,

        // Mannheimer Strassennamen mit Begrenzer C8 19
        "\\d+\\s?[.,]+\\s?" + NR_REGEX,

        // Mannheimer Strassennamen C8 19
        "^[a-zA-Z]\\s?\\d\\s?(?:\\D\\s?)?" + NR_REGEX,

        // year before house number
        "\\d{4}[\\s,.]" + NR_REGEX,

        // only matches number maybe preceded by [0-9]e
        "^(?:\\de){0,1}[^0-9]*?" + NR_REGEX, "([0-9]{1,5} ?[a-zA-Z]{0,1})"
    };

    private static final ImmutableList<Pattern> NUMBER_PATTERNS;

    private static final String[] ADDITIONAL_REGEXEN = new String[] {

        // @formatter:off
        "(c\\s?/\\s?o.*$)"
        // @formatter:on
    };

    private static final ImmutableList<Pattern> ADDITIONAL_PATTERNS;

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

    private static final ImmutableList<Pair<Pattern, String>> NUMBER_REPLACE_PATTERNS;

    private static final String[][] STREET_REPLACE_REGEXEN = new String[][] {

        // @formatter:off
        {"\\bst\\b[.\\s]*", "sint "},
        {"str\\b[.\\s]*", "straat "},
        {"\\bburg\\b[.\\s]*", "Burgemeester "},
        {"(?<!Schonde|Westerkappe)ln[.\\s]*\\b", "laan "},
        {"\\bG[.\\s]*(?=van Oostenstr)\\b", "Geertruyt "},
        {"\\bM[.\\s]*(?=Luther King)", "Martin "},
        {"\\bM[.\\s]*L[.\\s]*(?=King)", "Martin Luther "}
        // @formatter:on
    };

    private static final ImmutableList<Pair<Pattern, String>> STREET_REPLACE_PATTERNS;

    private static final String[][] COMPLETE_STREET_REPLACE_REGEXEN = new String[][] {
// @formatter:off
        // @formatter:on
    };

    private static final ImmutableList<Pair<Pattern, String>> COMPLETE_STREET_REPLACE_PATTERNS;

    private static final String[][] CITY_REPLACE_REGEXEN = new String[][] {
// @formatter:off
        {"\\bden\\s+bosch\\b", "'s-Hertogenbosch"},
        {"\\bden\\s+haag\\b", "'s-Gravenhage"}
        // @formatter:on
    };

    private static final ImmutableList<Pair<Pattern, String>> CITY_REPLACE_PATTERNS;

    private static final Pattern NOT_ALLOWED_CHARS_ZIP = Pattern.compile("[^\\da-zA-Z]");

    static {
        NUMBER_PATTERNS = compilePatterns(NUMBER_REGEXEN);
        ADDITIONAL_PATTERNS = compilePatterns(ADDITIONAL_REGEXEN, Pattern.CASE_INSENSITIVE);
        NUMBER_REPLACE_PATTERNS = compileReplacePattern(NUMBER_REPLACE_REGEXEN);
        STREET_REPLACE_PATTERNS = compileReplacePattern(STREET_REPLACE_REGEXEN, Pattern.CASE_INSENSITIVE);
        CITY_REPLACE_PATTERNS = compileReplacePattern(CITY_REPLACE_REGEXEN, Pattern.CASE_INSENSITIVE);
        COMPLETE_STREET_REPLACE_PATTERNS = compileReplacePattern(COMPLETE_STREET_REPLACE_REGEXEN,
                Pattern.CASE_INSENSITIVE);

    }

    @Override
    public String normalizeZip(final String zip) {
        String result = "";

        if (zip != null) {
            result = zip;
        }

        AddressProcessorUtil.replaceBackSlashesWithSlashes(result);

        final Matcher matcher = NOT_ALLOWED_CHARS_ZIP.matcher(result);
        result = matcher.replaceAll("");

        String res = trimAndCollapse(result);

        if (res.length() >= 6) {
            res = String.format("%s %s", res.substring(0, 4), res.substring(4, 6));
        }

        return res;
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
