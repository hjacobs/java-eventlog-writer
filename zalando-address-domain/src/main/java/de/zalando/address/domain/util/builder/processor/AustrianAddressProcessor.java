package de.zalando.address.domain.util.builder.processor;

import static de.zalando.address.domain.util.builder.AddressProcessorUtil.compilePatterns;
import static de.zalando.address.domain.util.builder.AddressProcessorUtil.compileReplacePattern;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableList;

import de.zalando.address.domain.util.builder.NumberPosition;

import de.zalando.utils.Pair;

public class AustrianAddressProcessor extends AbstractAddressProcessor {

    private static final Pattern SLASH = Pattern.compile("/");
    final Pattern FIRST_DASH_OTHERS_SLASH = Pattern.compile("^\\d+[a-zA-Z]?-");

    // This is what we need to collect.
    private static final String NR_REGEX = "((?:\\d+[\\p{Punct}\\s]+)*\\d+[\\p{Punct}\\s]*[a-zA-Z]?\\b)";

    // The order of the regexen is IMPORTANT!
    // Finds the following patterns: 4, 4-5, 4/5, 4/5/6, Straße des 17. Juni
    // 135,
    // C8 19, C8,19
    private static final String[] NUMBER_REGEXEN = new String[] {

        // @formatter:off
        "^\\s?[Ss]tr(?:a(?:ß|ss)e)?\\.?\\s\\d+[-.\\s\\/]+" + NR_REGEX, NR_REGEX + "[.,]*\\s\\d+[.\\s]*[OoGg]",
        "\\d+\\s?[.,]?\\s?[\\p{L}.]+\\s" + NR_REGEX, "\\d+\\s?[.,]+\\s?" + NR_REGEX,
        "^[a-zA-Z]\\s?\\d\\s?(?:\\D\\s?)?" + NR_REGEX, "\\d\\s?[,.]+\\s?" + NR_REGEX, NR_REGEX,
        "([0-9]{1,5} ?[a-zA-Z]{0,1})"
        // @formatter:on
    };

    private static final ImmutableList<Pattern> NUMBER_PATTERNS;

    private static final String[] ADDITIONAL_REGEXEN = new String[] {

        // @formatter:off
        "(c\\s?/\\s?o.*$)", "(\\d+[.\\s]*(?:etage|stock|\\bst\\b)\\.?)",
        "(\\d+[.\\s]*(?:obergeschoss|obergeschoß|untergeschoss|untergeschoß|og|ug)\\b[.\\s]*(?:[\\da-z]+[.\\s]*)?(?:rechts|links)?)",
        "(\\b(?:obergeschoss|obergeschoß|untergeschoss|untergeschoß|og|ug)\\b[.\\s]*(?:[\\da-z][.\\s]*)?(?:rechts|links)?)",
        "((?:vorderhaus|hinterhaus|gartenhaus|nebengebaeude|nebengeb[äaÄ]ude|seitenfluegel|seitenfl[üuÜ]gel|empfang|\\bvh\\b|\\bhh\\b|\\bgh\\b)\\.?)",
        "(?:[.,](\\s?bei\\s.*$))", "(?:\\sbei\\s.*(\\s?bei\\s.*$))",
        // @formatter:on
    };

    private static final ImmutableList<Pattern> ADDITIONAL_PATTERNS;

    private static final String[][] NUMBER_REPLACE_REGEXEN = new String[][] {

        // @formatter:off
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
        // @formatter:on
    };

    private static final ImmutableList<Pair<Pattern, String>> NUMBER_REPLACE_PATTERNS;

    private static final String[][] STREET_REPLACE_REGEXEN = new String[][] {

        // @formatter:off
        {"(?<=\\w[-\\s]?)stra[sß]+e\\b", "str."},
        {"\\bdo[ck]tor\\b(?!-(?:ruff|weis))", "dr."},
        {"\\bbgm[\\s.-]+", "bürgermeister-"},
        {"\\bpf\\s?[.-]+\\s?(?!-)", "pfarrer-"},
        {"\\bpf[\\s.-]+(?=-)", "pfarrer"},
        {"\\ba\\s?[.-]+\\s?(?=dürer|daniel)", "albrecht-"},
        {"\\ba\\s(?=dürer|daniel)", "albrecht-"},
        {"\\bs[\\s.-]+(?=allende)", "salvador-"}
        // @formatter:on
    };

    private static final ImmutableList<Pair<Pattern, String>> STREET_REPLACE_PATTERNS;

    private static final String[][] COMPLETE_STREET_REPLACE_REGEXEN = new String[][] {
// @formatter:off
        // @formatter:on
    };

    private static final ImmutableList<Pair<Pattern, String>> COMPLETE_STREET_REPLACE_PATTERNS;

    private static final String[][] CITY_REPLACE_REGEXEN = new String[0][];

    private static final ImmutableList<Pair<Pattern, String>> CITY_REPLACE_PATTERNS;

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
    protected String transformNumber(final String number) {

        // REBOOT-6243
        String result = super.transformNumber(number);
        final Matcher matcher = FIRST_DASH_OTHERS_SLASH.matcher(number);
        if (matcher.find()) {
            result = SLASH.matcher(result).replaceFirst("-");
        }

        return result;
    }

}
