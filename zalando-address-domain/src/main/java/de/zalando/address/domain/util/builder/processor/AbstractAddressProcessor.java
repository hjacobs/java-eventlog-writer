package de.zalando.address.domain.util.builder.processor;

import static com.google.common.base.Strings.nullToEmpty;

import static de.zalando.address.domain.util.builder.AddressProcessorUtil.ROMAN_NUMERALS;
import static de.zalando.address.domain.util.builder.AddressProcessorUtil.extractNumber;
import static de.zalando.address.domain.util.builder.AddressProcessorUtil.findNumber;
import static de.zalando.address.domain.util.builder.AddressProcessorUtil.removeNonDigitsAndTrim;
import static de.zalando.address.domain.util.builder.AddressProcessorUtil.removePatterns;
import static de.zalando.address.domain.util.builder.AddressProcessorUtil.replaceBackSlashesWithSlashes;
import static de.zalando.address.domain.util.builder.AddressProcessorUtil.replaceNotAllowedCharsAndTrim;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.WordUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import de.zalando.address.domain.util.builder.AddressGuess;
import de.zalando.address.domain.util.builder.Casing;
import de.zalando.address.domain.util.builder.NumberPosition;

import de.zalando.utils.Pair;

public abstract class AbstractAddressProcessor implements AddressProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractAddressProcessor.class);

    private static final Pattern SLASH_DASH = Pattern.compile("(?:\\d+ ?[-/.]+ ?){2,}\\d+");

    /**
     * Default pattern for not allowed street chars.
     */
    protected static final Pattern NOT_ALLOWED_DEFAULT_CHARS = Pattern.compile("[^-(),\\p{L}\\d./]");

    private static final char[] CAP_DELIMITERS = new char[] {'(', '-', ' ', '[', '.'};

    private static final Pattern DOT_OR_DASH = Pattern.compile("[-.]");

    private static final List<Pair<Pattern, String>> EMPTY_REPLACE_PATTERNS = Collections.unmodifiableList(Collections
                .<Pair<Pattern, String>>emptyList());

    /**
     * Returns the replace patterns, which are used to clean the cutted house number. This is a list of pairs, first is
     * the search regex, the second is the replacement.
     *
     * @return
     */
    protected abstract List<Pair<Pattern, String>> getNumberReplacePatterns();

    /**
     * Returns replace patterns for the street name, used for expanding or abbreviating parts of the street name. For
     * example: street into str.
     *
     * @return
     */
    protected abstract List<Pair<Pattern, String>> getStreetNameReplacePatterns();

    /**
     * Returns replace patterns for the street with number, used for expanding or abbreviating parts of the street name.
     * For example: 22 Hemlingford RD 23 Hemlingford RD into 22 Hemlingford Road 23 Hemlingford Road
     *
     * @return
     */
    protected abstract List<Pair<Pattern, String>> getCompleteStreetNameReplacePatterns();

    /**
     * Returns replace patterns for the city name, used for expanding or abbreviating parts of the city name. For
     * example: den Bosch into s'Hertogenbosch
     *
     * @return
     */
    protected abstract List<Pair<Pattern, String>> getCityReplacePatterns();

    /**
     * Returns a list of patterns, which are used to find the house number in the street string.
     *
     * @return
     */
    protected abstract List<Pattern> getNumberPatterns();

    /**
     * Returns a list of patterns to find the known additionals. These additionals are likely to be entered with the
     * street name.
     *
     * @return
     */
    protected abstract List<Pattern> getAdditionalPatterns();

    /**
     * Returns a list of replacement patterns for additional part of an address.
     *
     * @return
     */
    protected List<Pair<Pattern, String>> getAdditionalReplacePatterns() {
        return EMPTY_REPLACE_PATTERNS;
    }

    /**
     * The pattern of not allowed street chars. We have a default set @see: NOT_ALLOWED_CHARS
     *
     * @return
     */
    protected Pattern getNotAllowedStreetChars() {
        return NOT_ALLOWED_DEFAULT_CHARS;
    }

    /**
     * Returns a list of patterns to find the known additionals when written on city. These additionals are likely to be
     * province names.
     *
     * @return
     */
    protected List<Pattern> getCityAdditionalPatterns() {

        return Collections.emptyList();
    }

    /**
     * Return the patterns for casing. For example roman numbers should be upper cased.
     *
     * @return
     */
    protected List<Pair<Pattern, Casing>> getStreetCasePatterns() {
        return Collections.emptyList();
    }

    /**
     * Return the patterns for casing. For example roman numbers should be upper cased.
     *
     * @return
     */
    protected List<Pair<Pattern, Casing>> getCityCasePatterns() {
        return Collections.emptyList();
    }

    /**
     * Returns an array with the characters after which capitalization will occur.
     *
     * @return
     */
    protected char[] getCapitalizationDelimiters() {
        return CAP_DELIMITERS;
    }

    @Override
    public AddressGuess guessStreetNameAndNumber(final String street) {
        if (street == null) {
            return new AddressGuess("", "");
        }

        final String cleanedStreet = removeNotAllowedStreetChars(street);
        String number = findNumber(cleanedStreet, getNumberPatterns());
        final String name = extractNumber(cleanedStreet, number, getNumberPosition()).getFirst();
        number = transformNumber(number);

        return new AddressGuess(name, number);
    }

    @Override
    public Pair<String, String> normalizeStreet(final String streetWithNumber) {
        String cleanedStreet = removeNotAllowedStreetChars(streetWithNumber);

        cleanedStreet = replacePatterns(cleanedStreet, getCompleteStreetNameReplacePatterns());

        final Pair<String, String> additionalsRemoved = removePatterns(cleanedStreet, getAdditionalPatterns());

        // replace and cap
        String streetName = replacePatterns(additionalsRemoved.getFirst(), getStreetNameReplacePatterns());
        streetName = WordUtils.capitalizeFully(streetName.trim(), getCapitalizationDelimiters());
        streetName = fixCase(streetName);

        final String additionalPart;
        if (additionalsRemoved.getSecond() != null) {
            additionalPart = replacePatterns(additionalsRemoved.getSecond(), getAdditionalReplacePatterns());
        } else {
            additionalPart = null;
        }

        return Pair.of(streetName, additionalPart);
    }

    @Override
    public String normalizeHouseNumber(final String houseNumber) {
        String result = removeNotAllowedStreetChars(nullToEmpty(houseNumber));
        result = WordUtils.capitalizeFully(result.trim(), getCapitalizationDelimiters());
        return postProcessHouseNumber(result);
    }

    protected String postProcessHouseNumber(final String houseNumber) {
        return houseNumber;
    }

    @Override
    public String normalizeZip(final String zip) {
        return removeNonDigitsAndTrim(nullToEmpty(zip));
    }

    @Override
    public Pair<String, String> normalizeCity(final String city) {

        String result = removeNotAllowedStreetChars(city);

        final Pair<String, String> additionalsRemoved = removePatterns(result, getCityAdditionalPatterns());
        result = additionalsRemoved.getFirst();

        result = replacePatterns(result, getCityReplacePatterns());
        result = WordUtils.capitalizeFully(result.trim(), getCapitalizationDelimiters());

        result = fixCityCase(result);

        return Pair.of(result, additionalsRemoved.getSecond());
    }

    @Override
    public String normalizeCountryCode(final String countryCode) {
        String result = "";
        if (countryCode != null) {
            result = countryCode.trim().toUpperCase();
        }

        return result;
    }

    protected String transformNumber(final String number) {
        String result = replacePatterns(number, getNumberReplacePatterns());

        final Matcher matcher = SLASH_DASH.matcher(result);
        if (matcher.matches()) {
            result = DOT_OR_DASH.matcher(result).replaceAll("/");
        }

        result = result.toLowerCase().trim();

        LOG.debug("number [{}] transformed to [{}]", number, result);

        return result;
    }

    protected String fixCase(final String source) {
        @SuppressWarnings("unchecked")
        final List<Pair<Pattern, Casing>> streetCasePatterns = Lists.newArrayList(Pair.of(ROMAN_NUMERALS,
                    Casing.UPPER));
        streetCasePatterns.addAll(getStreetCasePatterns());
        return fixCase(source, streetCasePatterns);
    }

    protected String fixCityCase(final String source) {
        final List<Pair<Pattern, Casing>> cityCasePatterns = getCityCasePatterns();
        return fixCase(source, cityCasePatterns);
    }

    protected String fixCase(final String source, final List<Pair<Pattern, Casing>> streetCasePatterns) {
        final StringBuilder result = new StringBuilder(source);
        if (streetCasePatterns != null) {
            for (final Pair<Pattern, Casing> casePair : streetCasePatterns) {
                final Matcher matcher = casePair.getFirst().matcher(source);
                while (matcher.find()) {
                    String match = "";
                    switch (casePair.getSecond()) {

                        case LOWER :
                            match = matcher.group(0).toLowerCase();
                            break;

                        case UPPER :
                            match = matcher.group(0).toUpperCase();
                            break;

                        case CAPITALIZED :
                            match = WordUtils.capitalizeFully(matcher.group(0), getCapitalizationDelimiters());
                            break;
                    }

                    result.replace(matcher.start(), matcher.end(), match);
                }
            }
        }

        return result.toString();
    }

    protected static String replacePatterns(final String source, final List<Pair<Pattern, String>> patterns) {
        String result = source;

        if (patterns != null) {
            int i = 0;
            for (final Pair<Pattern, String> replace : patterns) {
                final Pattern pattern = replace.getFirst();
                final String substitute = replace.getSecond();
                final String soFar = result;
                final Matcher matcher = pattern.matcher(result);
                result = matcher.replaceAll(substitute);
                if (LOG.isTraceEnabled() && !soFar.equals(result)) {
                    LOG.trace("'{}' transformed to '{}' with substitute pattern #{} [{}] -> [{}]",
                        new Object[] {soFar, result, i, pattern.pattern(), substitute});
                }

                i++;
            }
        }

        return result;
    }

    protected String removeNotAllowedStreetChars(final String streetWithNumber) {
        String result = "";

        if (streetWithNumber != null) {
            result = streetWithNumber;
        }

        result = replaceBackSlashesWithSlashes(result);
        result = replaceNotAllowedCharsAndTrim(result, getNotAllowedStreetChars());

        return result;
    }

    @Override
    public String joinStreetWithHouseNumber(final String streetName, final String houseNumber) {
        String result;
        if (getNumberPosition() == NumberPosition.LEFT) {
            result = String.format("%s %s", Strings.nullToEmpty(houseNumber), streetName).trim();
        } else {
            result = String.format("%s %s", streetName, Strings.nullToEmpty(houseNumber)).trim();
        }

        return result;
    }

}
