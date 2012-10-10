package de.zalando.address.domain.util.builder.processor;

import static de.zalando.address.domain.util.builder.AddressProcessorUtil.compilePatterns;
import static de.zalando.address.domain.util.builder.AddressProcessorUtil.compileReplacePattern;
import static de.zalando.address.domain.util.builder.AddressProcessorUtil.simpleReplaceRegexp;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.WordUtils;

import com.google.common.collect.ImmutableList;

import de.zalando.address.domain.util.builder.AddressGuess;
import de.zalando.address.domain.util.builder.AddressProcessorUtil;
import de.zalando.address.domain.util.builder.NumberPosition;

import de.zalando.utils.Pair;

public class BritishAddressProcessor extends AbstractAddressProcessor {

    // we define an own allowed chars pattern for GB
    private static final Pattern NOT_ALLOWED_GB_STREET_CHARS = Pattern.compile("[^-(),\\p{L}\\d./'`]");

    // This is what we need to collect.
    private static final String NR_REGEX = "((?:\\d+[\\p{Punct}\\s]+)*\\d+[\\p{Punct}\\s]*[a-zA-Z]?\\b)";
    private static final String[] NUMBER_REGEXEN = new String[] {
        "(\\bFlat\\b\\s+" + NR_REGEX + "([/,]\\d+)?)", NR_REGEX + "\\s?[.,]?\\s?[\\p{L}.]+",
        NR_REGEX + "\\s?\\d+\\s?[.,]+", NR_REGEX + "^[a-zA-Z]\\s?\\d\\s?(?:\\D\\s?)?", NR_REGEX + "\\d\\s?[,.]+\\s?",
        NR_REGEX, "([0-9]{1,5} ?[a-zA-Z]{0,1})"
    };

    private static final ImmutableList<Pattern> NUMBER_PATTERNS;

    private static final String[] ADDITIONAL_REGEXEN = new String[] {};

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
        {"^(\\d)[-/ ]+([a-zA-Z]+)", "$1$2"},
    };

    private static final ImmutableList<Pair<Pattern, String>> NUMBER_REPLACE_PATTERNS;

    private static String _wrapAbbrev(final String abbreviation) {
        return "\\b" + abbreviation + "\\b\\.?";
    }

    private static final String[][] STREET_REPLACE_REGEXEN = new String[][] {

        {_wrapAbbrev("st") + '$', "Street"}, // replace at the end of the street
    };

    private static final ImmutableList<Pair<Pattern, String>> STREET_REPLACE_PATTERNS;

    private static final String[][] COMPLETE_STREET_REPLACE_REGEXEN = new String[][] {

        {_wrapAbbrev("rd"), "Road"},        // replace at the end of the street
        {_wrapAbbrev("av"), "Avenue"},      // replace at the end of the street
        {_wrapAbbrev("ave"), "Avenue"},     // replace at the end of the street
        {_wrapAbbrev("cl"), "Close"},       // replace at the end of the street
        {_wrapAbbrev("cotts"), "Cottages"}, // replace at the end of the street
        {_wrapAbbrev("ct"), "Court"},       // replace at the end of the street
        {_wrapAbbrev("cres"), "Crescent"},  // replace at the end of the street
        {_wrapAbbrev("dr"), "Drive"},       // replace at the end of the street
        {_wrapAbbrev("est"), "Estate"},     // replace at the end of the street
        {_wrapAbbrev("fld"), "Field"},      // replace at the end of the street
        {_wrapAbbrev("flds"), "Fields"},    // replace at the end of the street
        {_wrapAbbrev("gdns"), "Gardens"},   // replace at the end of the street
        {_wrapAbbrev("grn"), "Green"},      // replace at the end of the street
        {_wrapAbbrev("gr"), "Grove"},       // replace at the end of the street
        {_wrapAbbrev("la"), "Lane"},        // replace at the end of the street
        {_wrapAbbrev("mt"), "Mount"},       // replace at the end of the street
        {_wrapAbbrev("pde"), "Parade"},     // replace at the end of the street
        {_wrapAbbrev("pk"), "Park"},        // replace at the end of the street
        {_wrapAbbrev("pl"), "Place"},       // replace at the end of the street
        {_wrapAbbrev("sq"), "Square"},      // replace at the end of the street
        {_wrapAbbrev("ter"), "Terrace"},    // replace at the end of the street
        {_wrapAbbrev("wk"), "Walk"},        // replace at the end of the street
        {_wrapAbbrev("wy"), "Way"},         // replace at the end of the street
        {_wrapAbbrev("w"), "West"},         // replace at the end of the street
        {_wrapAbbrev("e"), "East"},         // replace at the end of the street
        {_wrapAbbrev("n"), "North"},        // replace at the end of the street
        {"(^|\\s)s(\\.|\\b)", " South"}
    };

    private static final ImmutableList<Pair<Pattern, String>> COMPLETE_STREET_REPLACE_PATTERNS;

    private static final String[][] CITY_REPLACE_REGEXEN = new String[][] {
        {"\\b(\\w+)\\b,\\s*\\b(\\w+)\\b", "$1, $2"},
        {"\\b(\\w+)\\b,\\s*\\b(\\w+)\\b,\\s*\\b(\\w+)\\b", "$1, $2, $3"},

        // we remove the county name from the city (database recocognizes
        // only localities)
        {"\\bBlackburn with Darwen\\b", ""},
        {"\\bBuckinghamshire\\b", ""},
        {"\\bBath and North East Somerset\\b", ""},
        {"\\bBerkshire\\b", ""},
        {"\\bCornwall\\b", ""},
        {"\\bDevon\\b", ""},
        {"\\bDorset\\b", ""},
        {"\\bEast Sussex\\b", ""},
        {"\\bNorthumberland\\b", ""},
        {"\\bTyne and Wear\\b", ""},
        {"\\bCumbria\\b", ""},
        {"\\bLancashire\\b", ""},
        {"\\bWest Yorkshire\\b", ""},
        {"\\bNorth Yorkshire\\b", ""},
        {"\\bRedcar and Cleveland\\b", ""},
        {"\\bEast Riding of Yorkshire\\b", ""},
        {"\\bKingston upon Hull\\b", ""},
        {"\\bNorth Lincolnshire\\b", ""},
        {"\\bNorth East Lincolnshire\\b", ""},
        {"\\bLincolnshire\\b", ""},
        {"\\bNottinghamshire\\b", ""},
        {"\\bSouth Yorkshire\\b", ""},
        {"\\bDerbyshire\\b", ""},
        {"\\bGreater Manchester\\b", ""},
        {"\\bMerseyside\\b", ""},
        {"\\bHalton\\b", ""},
        {"\\bCheshire West and Chester\\b", ""},
        {"\\bCheshire East\\b", ""},
        {"\\bShropshire\\b", ""},
        {"\\bTelford and Wrekin\\b", ""},
        {"\\bStaffordshire\\b", ""},
        {"\\bWest Midlands\\b", ""},
        {"\\bWarwickshire\\b", ""},
        {"\\bLeicestershire\\b", ""},
        {"\\bRutland\\b", ""},
        {"\\bNorthamptonshire\\b", ""},
        {"\\bCambridgeshire\\b", ""},
        {"\\bNorfolk\\b", ""},
        {"\\bSuffolk\\b", ""},
        {"\\bEssex\\b", ""},
        {"\\bThurrock\\b", ""},
        {"\\bHertfordshire\\b", ""},
        {"\\bCentral Bedfordshire\\b", ""},
        {"\\bOxfordshire\\b", ""},
        {"\\bGloucestershire\\b", ""},
        {"\\bWorcestershire\\b", ""},
        {"\\bHerefordshire\\b", ""},
        {"\\bSouth Gloucestershire\\b", ""},
        {"\\bNorth Somerset\\b", ""},
        {"\\bWiltshire\\b", ""},
        {"\\bGreater London\\b", ""},
        {"\\bMedway\\b", ""},
        {"\\bKent\\b", ""},
        {"\\bWest Sussex\\b", ""},
        {"\\bSurrey\\b", ""},
        {"\\bHampshire\\b", ""},
        {"\\bIsle of Wight\\b", ""},
        {"\\bSomerset\\b", ""},
        {"\\bTorbay\\b", ""},

        // we do not apply translation over the following counties because
        // there are cities with the same name
        // {"\\bBournemouth\\b", ""}, -- has city
        // {"\\bBrighton\\b", ""}, -- has city
        // {"\\bDurham\\b", ""}, -- has city
        // {"\\bDarlington\\b", ""}, -- has city
        // {"\\bStockton-on-Tees\\b", ""}, -- has city
        // {"\\bMiddlesbrough\\b", ""}, -- has city
        // {"\\bHartlepool\\b", ""}, -- has city
        // {"\\bWarrington\\b", ""}, -- has city
        // {"\\bStoke-on-Trent\\b", ""}, -- has city
        // {"\\bLeicester\\b", ""}, -- has city
        // {"\\bPeterborough\\b", ""}, -- has city
        // {"\\bSouthend-on-Sea\\b", ""}, -- has city
        // {"\\bLuton\\b", ""}, -- has city
        // {"\\bMilton Keynes\\b", ""}, -- has city
        // {"\\bSwindon\\b", ""}, -- has city
        // {"\\bSouthampton\\b", ""}, -- has city
        // {"\\bPortsmouth\\b", ""}, -- has city
        // {"\\bPlymouth\\b", ""}, -- has city
        // {"\\bYork\\b", ""}, -- has city
        // {"\\bNottingham\\b", ""}, -- has city
        // {"\\bDerby\\b", ""}, -- has city
        // {"\\bBristol\\b", ""}, -- has city
        // {"\\bBedford\\b", ""}, - has city
        // {"\\bBlackpool\\b", ""}, -- has city
        // {"\\bPoole\\b", ""}, -- has city
        {"(.*),\\s*$", "$1"}
    };

    private static final ImmutableList<Pair<Pattern, String>> CITY_REPLACE_PATTERNS;

    private static final String[] ZIP_FORMAT_PATTERN = {"(.*)(.{3})$", "$1 $2"};

    private static final Pattern NUMBER_PART_PATTERN;

    static {
        NUMBER_PATTERNS = compilePatterns(NUMBER_REGEXEN, Pattern.CASE_INSENSITIVE);
        ADDITIONAL_PATTERNS = compilePatterns(ADDITIONAL_REGEXEN, Pattern.CASE_INSENSITIVE);
        NUMBER_REPLACE_PATTERNS = compileReplacePattern(NUMBER_REPLACE_REGEXEN);
        STREET_REPLACE_PATTERNS = compileReplacePattern(STREET_REPLACE_REGEXEN, Pattern.CASE_INSENSITIVE);
        COMPLETE_STREET_REPLACE_PATTERNS = compileReplacePattern(COMPLETE_STREET_REPLACE_REGEXEN,
                Pattern.CASE_INSENSITIVE);
        CITY_REPLACE_PATTERNS = compileReplacePattern(CITY_REPLACE_REGEXEN, Pattern.CASE_INSENSITIVE);

        NUMBER_PART_PATTERN = Pattern.compile("^([0-9]+(?:-[0-9]+)?[a-zA-Z]*)$", Pattern.CASE_INSENSITIVE);
    }

    @Override
    public String normalizeZip(final String zip) {

        String result = "";

        if (zip != null) {
            result = zip;
        }

        AddressProcessorUtil.replaceNotAllowedCharsAndTrim(result, NOT_ALLOWED_DEFAULT_CHARS);
        result = result.toUpperCase();
        result = result.replaceAll("[\\s, -]+", "");

        result = simpleReplaceRegexp(ZIP_FORMAT_PATTERN[0], ZIP_FORMAT_PATTERN[1], result);

        return result;
    }

    @Override
    protected Pattern getNotAllowedStreetChars() {
        return NOT_ALLOWED_GB_STREET_CHARS;
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

    @Override
    public Pair<String, String> normalizeStreet(final String streetWithNumber) {

        String cleanedStreet = removeNotAllowedStreetChars(streetWithNumber);
        cleanedStreet = replacePatterns(cleanedStreet, getCompleteStreetNameReplacePatterns());

        // replace and cap
        String streetName = replacePatterns(cleanedStreet.trim(), getStreetNameReplacePatterns());
        streetName = WordUtils.capitalizeFully(streetName, getCapitalizationDelimiters());
        streetName = fixCase(streetName);
        return Pair.of(streetName, null);
    }

    /**
     * For UK Addresses we treat all inside street fields.
     */
    @Override
    public AddressGuess guessStreetNameAndNumber(final String street) {

        return new AddressGuess(street, null);
    }

    /**
     * We treat UK addresses as combinations of street1 and street2, with no special treatment of house numbers, etc.
     * Experian, on the other hand, need there field correcty. Here we try to split a given address (supposed to be a
     * complete street address) in street name, house number and house name (which is everything else).
     */

    public static String guessStreetName(final String raw) {
        String[] parts = raw.split(" ");

        StringBuilder tmp = new StringBuilder();

        for (int i = parts.length - 1; i >= 0; i--) {
            Matcher matcher = NUMBER_PART_PATTERN.matcher(parts[i]);
            boolean numberFound = matcher.find();

            if (numberFound || parts[i].trim().endsWith(",")) {
                return tmp.toString().trim();
            }

            tmp.insert(0, parts[i] + ' ');
        }

        return null;
    }

    public static String guessHouseNumber(final String raw) {
        String[] parts = raw.split(" ");

        for (int i = parts.length - 1; i >= 0; i--) {

            final String part = parts[i];
            Matcher matcher = NUMBER_PART_PATTERN.matcher(part);
            boolean numberFound = matcher.find();

            if (part.trim().endsWith(",")) {

                // drop house number search. there is none here.
                break;
            }

            if (numberFound) {
                return part.trim();
            }
        }

        return null;
    }

    public static String guessHouseName(final String raw) {
        String[] parts = raw.split(" ");

        String tmp = "";
        boolean foundRest = false;

        for (int i = parts.length - 1; i >= 0; i--) {

            if (foundRest) {

                tmp = parts[i] + ' ' + tmp;

                // we already found our starting point. there's not need to take care of anything else in the loop.
                continue;
            }

            Matcher matcher = NUMBER_PART_PATTERN.matcher(parts[i]);
            boolean numberFound = matcher.find();

            boolean endsWithComma = parts[i].trim().endsWith(",");
            if (numberFound) {

                // now it starts to aggregate
                foundRest = true;
            } else if (endsWithComma) {
                foundRest = true;

                // start building our result.
                tmp = parts[i];
            }

        }

        tmp = tmp.trim();

        // if the last character is "," we cut it off.
        if (tmp.endsWith(",")) {
            tmp = tmp.substring(0, tmp.length() - 1);
        }

        return tmp;
    }
}
