package de.zalando.address.domain.util.builder.processor;

import static de.zalando.address.domain.util.builder.AddressProcessorUtil.compilePatterns;
import static de.zalando.address.domain.util.builder.AddressProcessorUtil.compileReplacePattern;

import java.util.List;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableList;

import de.zalando.address.domain.util.builder.NumberPosition;

import de.zalando.utils.Pair;

public class FrenchAddressProcessor extends AbstractAddressProcessor {

    private static final String NR_REGEX = "((?:\\d+[\\p{Punct}\\s]+)*\\d+[\\p{Punct}\\s]*(?:[a-z]\\b?\\s+)?"
            + "(?:[,. ]*(?:quater|bis|ter|quinquies))?\\b)";

    private static final String[] NUMBER_REGEXEN = new String[] {
        NR_REGEX + "\\s?[.,]?\\s?[\\p{L}.]+", NR_REGEX + "\\s?\\d+\\s?[.,]+",
        NR_REGEX + "^[a-zA-Z]\\s?\\d\\s?(?:\\D\\s?)?", NR_REGEX + "\\d\\s?[,.]+\\s?", NR_REGEX,
        "([0-9]{1,5} ?[a-zA-Z]{0,1})"
    };

    private static final ImmutableList<Pattern> NUMBER_PATTERNS;

    private static final String[] ADDITIONAL_REGEXEN = new String[] {

        "[-/\\s]*((?:bâtiment|batiment|appartement|appt|apt)\\b[.\\s]*[\\da-z]+\\b)",
        "[-/\\s]*((?:bât\\b|bat\\b|\\bhall\\b)\\.?\\s?\\d+[a-z]?\\b)",
        "[-/\\s]*((?:bât\\b|bat\\b|\\bhall\\b)\\.\\s?[\\da-z]+\\b)",
        "[-/]+\\s?((?:bât\\b|bat\\b|\\bhall\\b)[.\\s\\da-z]+\\b)", "[-/\\s]*([eÉé]tage\\s*\\d+\\.?)",
        "[-/\\s]*((BP|B.P.)\\s*\\d+\\.?)", "[-/\\s]*((ét|etg|[eé]tage)\\s*\\d+\\.?)",
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
        {"(\\d)[-/ ]+([a-zA-Z]\\b)", "$1$2"},
        {"(.*\\d)[-/.]+$", "$1"},
        {"(\\d)(quater|bis|ter)", "$1 $2"}
    };

    private static final ImmutableList<Pair<Pattern, String>> NUMBER_REPLACE_PATTERNS;

    private static final String[][] STREET_REPLACE_REGEXEN = new String[][] {

        // @formatter:off
        {"\\bR[\\s.]+P[\\s.]+", "Reverend Pere "},
        {"\\bRP[\\s.]*\\b", "Reverend Pere "},
        {"\\bbld[\\s.]*\\b", "Boulevard "},
        {"\\bblvd[\\s.]*\\b", "Boulevard "},
        {"\\bboul[\\s.]*\\b", "Boulevard "},
        {"(?<=\\d[a-z]? )(?:lotissmt|lotissm|logt|lot)[\\s.]*\\b", "Lotissement "},
        {"\\bbd\\.", "Boulevard "},
        {"\\bal\\.", "Allee"},
        {"\\bav\\.", "Avenue"},
        {"\\bave\\.", "Avenue"},
        {"(?<!saint )\\bave\\b(?! maria)", "Avenue"},
        {"\\bimp\\.", "Impasse"},
        {"\\bche\\b\\s*\\.\\s+", "Chemin"},
        {"\\bche\\b (?!guevara)", "Chemin"},
        {"\\bchem\\b\\s*\\.\\s+", "Chemin "},
        {"\\bgd\\b", "Grand"},
        {"\\bgr\\b", "Grand"},
        {"\\br[eé]s(i{0,1})\\b", "Residence"},
        {"\\bsq\\b", "Square"},
        {"\\bst\\b", "Saint"},
        {"\\bste\\b", "Sainte"},
        {"\\bfbg\\b", "Faubourg"},
        {"\\brte\\b", "Route"},
        {"\\brd\\b", "Rond"},
        // @formatter:on
    };

    private static final ImmutableList<Pair<Pattern, String>> STREET_REPLACE_PATTERNS;

    private static final String[][] COMPLETE_STREET_REPLACE_REGEXEN = new String[][] {};

    private static final ImmutableList<Pair<Pattern, String>> COMPLETE_STREET_REPLACE_PATTERNS;

    private static final String[][] CITY_REPLACE_REGEXEN = new String[0][];

    // TODO Saint to St, sainte to ste
    private static final ImmutableList<Pair<Pattern, String>> CITY_REPLACE_PATTERNS;

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
        return NumberPosition.LEFT;
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
