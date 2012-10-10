package de.zalando.address.domain.util.builder.processor;

import static de.zalando.address.domain.util.builder.AddressProcessorUtil.compileCasingPatterns;
import static de.zalando.address.domain.util.builder.AddressProcessorUtil.compilePatterns;
import static de.zalando.address.domain.util.builder.AddressProcessorUtil.compileReplacePattern;

import java.util.List;
import java.util.regex.Pattern;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import de.zalando.address.domain.util.builder.Casing;
import de.zalando.address.domain.util.builder.NumberPosition;

import de.zalando.utils.Pair;

public class SpanishAddressProcessor extends AbstractAddressProcessor {

    // recognize ° (wrong U+00B0) as º (correct U+00BA)
    // we define an own allowed chars pattern for ES
    private static final Pattern NOT_ALLOWED_ES_STREET_CHARS = Pattern.compile("[^-(),\\p{L}\u00B0\u00BA\\d./]");

    // This is what we need to collect.
    private static final String NR_REGEX = "((?:\\d+[\\p{Punct}\\s]+)*\\d+[\\p{Punct}\\s]*[a-zA-Z]*\\b)";

    private static final String[] NUMBER_REGEXEN = new String[] {
        "[[^,].]*,([[^,].]+)(:?,.*)?", ",\\s*N\u00BA" + NR_REGEX, NR_REGEX + "\\s?,+\\s?", NR_REGEX
    };

    private static final ImmutableList<Pattern> ADDITIONAL_PATTERNS;

    private static final String[] LOWER_CASE_PATTERNS = new String[] {
        "\\bdel?\\b", "\\blas\\b", "\\bi\\b", "\\bles\\b"
    };

    private static final String[] CAPITALIZED_CASE_PATTERNS = new String[] {"^CL"};

    private static final String[] ADDITIONAL_REGEXEN = new String[] {"[[^,].]*,[[^,].]*,(.*)"};

    private static final String[][] NUMBER_REPLACE_REGEXEN = new String[][] {};

    private static final String[][] STREET_REPLACE_REGEXEN = new String[][] {
        {"\\bC[\\s.]*/[\\s.]*\\b", "Calle "},
        {"\\bC[\\s.]*l?\\.[\\s.]*\\b", "Calle "},
        {"\\bctra[\\s.]*\\b", "Carretera "},
        {"\\bPl\\.[\\s.]*\\b", "Plaza "},
        {"\\bPlza\\.[\\s.]*\\b", "Plaza "},
        {"\\bPlz\\.[\\s.]*\\b", "Plaza "},
        {"\\bPza\\.[\\s.]*\\b", "Plaza "},
        {"\\bAv\\.[\\s.]*\\b", "Avenida "},
        {"\\bAvd\\.[\\s.]*\\b", "Avenida "},
        {"\\bAvda\\.[\\s.]*\\b", "Avenida "},
        {"\\bP\\.\u00B0[\\s.]*\\b", "Paseo "},
        {"\\bP\\.\u00BA[\\s.]*\\b", "Paseo "},
        {"\\bBlq\\.[\\s.]*\\b", "Bloque "},
        {"\\bPta\\.[\\s.]*\\b", "Puerta "},
        {"\\bplta\\.[\\s.]*\\b", "Planta "},
        {"\\bedif\\.[\\s.]*\\b", "Edificio "},
    };

    private static final String[][] COMPLETE_STREET_REPLACE_REGEXEN = new String[][] {
        {"\u00B0", "\u00BA"}
    };

    private static final String[][] CITY_REPLACE_REGEXEN = new String[][] {
        {"\\b(\\w+)\\b[\\s/^w]*,[\\s/^w]*\\b(\\w+)\\b\\s*", "$1, $2"},
    };

    private static final ImmutableList<Pattern> NUMBER_PATTERNS;
    private static final ImmutableList<Pair<Pattern, Casing>> STREET_CASE_PATTERNS;
    private static final ImmutableList<Pair<Pattern, String>> NUMBER_REPLACE_PATTERNS;
    private static final ImmutableList<Pair<Pattern, String>> STREET_REPLACE_PATTERNS;

    private static final ImmutableList<Pair<Pattern, String>> COMPLETE_STREET_REPLACE_PATTERNS;
    private static final ImmutableList<Pair<Pattern, String>> CITY_REPLACE_PATTERNS;

    static {
        NUMBER_PATTERNS = compilePatterns(NUMBER_REGEXEN, Pattern.CASE_INSENSITIVE);
        ADDITIONAL_PATTERNS = compilePatterns(ADDITIONAL_REGEXEN, Pattern.CASE_INSENSITIVE);
        NUMBER_REPLACE_PATTERNS = compileReplacePattern(NUMBER_REPLACE_REGEXEN);
        STREET_REPLACE_PATTERNS = compileReplacePattern(STREET_REPLACE_REGEXEN, Pattern.CASE_INSENSITIVE);

        COMPLETE_STREET_REPLACE_PATTERNS = compileReplacePattern(COMPLETE_STREET_REPLACE_REGEXEN,
                Pattern.CASE_INSENSITIVE);
        CITY_REPLACE_PATTERNS = compileReplacePattern(CITY_REPLACE_REGEXEN, Pattern.CASE_INSENSITIVE);
        STREET_CASE_PATTERNS = compileCasingPatterns(LOWER_CASE_PATTERNS, null, CAPITALIZED_CASE_PATTERNS,
                Pattern.CASE_INSENSITIVE);
    }

    @Override
    public NumberPosition getNumberPosition() {

        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Pattern getNotAllowedStreetChars() {
        return NOT_ALLOWED_ES_STREET_CHARS;
    }

    @Override
    protected List<Pair<Pattern, String>> getNumberReplacePatterns() {
        return NUMBER_REPLACE_PATTERNS;
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
    protected List<Pattern> getNumberPatterns() {
        return NUMBER_PATTERNS;
    }

    @Override
    protected List<Pattern> getAdditionalPatterns() {
        return ADDITIONAL_PATTERNS;
    }

    @Override
    protected List<Pair<Pattern, Casing>> getStreetCasePatterns() {
        return STREET_CASE_PATTERNS;
    }

    @Override
    protected List<Pair<Pattern, Casing>> getCityCasePatterns() {
        return STREET_CASE_PATTERNS;
    }

    @Override
    public String joinStreetWithHouseNumber(final String streetName, final String houseNumber) {
        return String.format("%s, %s", streetName, Strings.nullToEmpty(houseNumber)).trim();
    }
}
