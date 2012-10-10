package de.zalando.address.domain.util.builder;

import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.base.Strings.nullToEmpty;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;

import de.zalando.address.domain.util.builder.processor.AddressProcessor;
import de.zalando.address.domain.util.builder.processor.AustrianAddressProcessor;
import de.zalando.address.domain.util.builder.processor.BelgianAddressProcessor;
import de.zalando.address.domain.util.builder.processor.BritishAddressProcessor;
import de.zalando.address.domain.util.builder.processor.DanishAddressProcessor;
import de.zalando.address.domain.util.builder.processor.DefaultAddressProcessor;
import de.zalando.address.domain.util.builder.processor.DutchAddressProcessor;
import de.zalando.address.domain.util.builder.processor.FinnishAddressProcessor;
import de.zalando.address.domain.util.builder.processor.FrenchAddressProcessor;
import de.zalando.address.domain.util.builder.processor.GermanAddressProcessor;
import de.zalando.address.domain.util.builder.processor.ItalianAddressProcessor;
import de.zalando.address.domain.util.builder.processor.NorwegianAddressProcessor;
import de.zalando.address.domain.util.builder.processor.PolishAddressProcessor;
import de.zalando.address.domain.util.builder.processor.SpanishAddressProcessor;
import de.zalando.address.domain.util.builder.processor.SwedishAddressProcessor;
import de.zalando.address.domain.util.builder.processor.SwissAddressProcessor;

import de.zalando.domain.address.AddressType;
import de.zalando.domain.address.AddressWithDetails;
import de.zalando.domain.address.Similarity;
import de.zalando.domain.globalization.ISOCountryCode;

import de.zalando.utils.Pair;
import de.zalando.utils.Range;

/**
 * ATTENTION if you change this builder then change
 * {@link de.zalando.commons.backend.address.GsonAddressAdapter GsonAddressAdapter} as well.
 */
public class AddressBuilder {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(AddressBuilder.class);

    private static final Map<ISOCountryCode, AddressProcessor> PROCESSOR_MAP;
    private static final Set<Character> VALID_CHAR_SPACE_SET;
    private static final char CHAR_SPACE = (char) 0x0020;

    private static final AddressProcessor DEFAULT_PROCESSOR = new DefaultAddressProcessor();
    private static final String UK = "UK";

    static {
        PROCESSOR_MAP = new EnumMap<ISOCountryCode, AddressProcessor>(ISOCountryCode.class);
        PROCESSOR_MAP.put(ISOCountryCode.AT, new AustrianAddressProcessor());
        PROCESSOR_MAP.put(ISOCountryCode.BE, new BelgianAddressProcessor());
        PROCESSOR_MAP.put(ISOCountryCode.CH, new SwissAddressProcessor());
        PROCESSOR_MAP.put(ISOCountryCode.DE, new GermanAddressProcessor());
        PROCESSOR_MAP.put(ISOCountryCode.DK, new DanishAddressProcessor());
        PROCESSOR_MAP.put(ISOCountryCode.FR, new FrenchAddressProcessor());
        PROCESSOR_MAP.put(ISOCountryCode.GB, new BritishAddressProcessor());
        PROCESSOR_MAP.put(ISOCountryCode.IT, new ItalianAddressProcessor());
        PROCESSOR_MAP.put(ISOCountryCode.NL, new DutchAddressProcessor());
        PROCESSOR_MAP.put(ISOCountryCode.PL, new PolishAddressProcessor());
        PROCESSOR_MAP.put(ISOCountryCode.SE, new SwedishAddressProcessor());
        PROCESSOR_MAP.put(ISOCountryCode.ES, new SpanishAddressProcessor());
        PROCESSOR_MAP.put(ISOCountryCode.FI, new FinnishAddressProcessor());
        PROCESSOR_MAP.put(ISOCountryCode.NO, new NorwegianAddressProcessor());

        VALID_CHAR_SPACE_SET = Sets.newHashSet('\n', '\r', '\t');
    }

    private static final de.zalando.domain.address.Address EMPTY = new AddressImpl();

    private final AddressProcessor processor;

    private String streetName;
    private String streetAddition;
    private String streetAdditionGuess;
    private String streetWithHouseNumber;
    private String houseNumber;
    private String city;
    private String zip;

    private boolean noNormalization = false;
    private final ISOCountryCode countryCode;

    private Similarity similarity;
    private boolean valid = true;
    private Range houseNumberRange;

    private boolean wildcardStreet = false;
    private boolean wildcardHouseNumber = false;
    private boolean wildcardCity = false;

    public static AddressBuilder forCountry(final ISOCountryCode countryCode) {
        return new AddressBuilder(countryCode);
    }

    public static AddressBuilder forCountry(final String countryCode) {
        return new AddressBuilder(countryCode);
    }

    public static AddressBuilder forAddress(final de.zalando.domain.address.Address address) {

        if (address == null) {
            throw new IllegalArgumentException("Address cannot be null");
        }

        final AddressBuilder builder = new AddressBuilder(address.getCountryCode());
        builder.noNormalization().city(address.getCity()).streetName(address.getStreetName())
               .streetWithHouseNumber(address.getStreetWithNumber()).houseNumber(address.getHouseNumber())
               .streetAddition(address.getAdditional()).zip(address.getZip());
        return builder;
    }

    public static AddressBuilder forAddressWithNormalization(final de.zalando.domain.address.Address address) {
        final AddressBuilder builder = new AddressBuilder(address.getCountryCode());
        builder.city(address.getCity()).streetWithHouseNumber(address.getStreetWithNumber())
               .streetAddition(address.getAdditional()).zip(address.getZip());
        return builder;
    }

    public static AddressBuilder forAddress(final AddressWithDetails address) {
        return forAddress((de.zalando.domain.address.Address) address).similarity(address.getSimilarity());
    }

    /**
     * Creates a new builder instance for the given country.
     *
     * @param  countryCode
     */
    AddressBuilder(final ISOCountryCode countryCode) {
        this.countryCode = countryCode;
        processor = createProcessor(countryCode);
    }

    /**
     * Creates a new builder instance for the given country. If the given countryCode is invalid it will use the default
     * processor.
     *
     * @param  countryCode  in ISO alpha 2 format, ie.: DE, HU
     */
    AddressBuilder(final String countryCode) {
        ISOCountryCode code = null;
        AddressProcessor proc = null;
        try {
            final String codeString = nullToEmpty(countryCode).trim().toUpperCase();
            if (UK.equals(codeString)) {
                LOG.warn("UK country code is illegal, mapping it to GB");
                code = ISOCountryCode.GB;
            } else {
                code = ISOCountryCode.valueOf(codeString);
            }

            proc = createProcessor(code);
        } catch (final Exception e) {
            LOG.error("Illegal country code {}", countryCode, e);
            proc = DEFAULT_PROCESSOR;
        }

        this.processor = proc;
        this.countryCode = code;
    }

    /**
     * Returns an empty address. Intended for testing purposes.
     *
     * @return
     */
    public static de.zalando.domain.address.Address emptyAddress() {
        return EMPTY;
    }

    private static AddressProcessor createProcessor(final ISOCountryCode myCountryCode) {
        AddressProcessor result = DEFAULT_PROCESSOR;
        if (PROCESSOR_MAP.containsKey(myCountryCode)) {
            result = PROCESSOR_MAP.get(myCountryCode);
        }

        return result;
    }

    /**
     * Returns the constructed address. Throws an IllegalStateException, if already used.
     *
     * @return  address
     */
    public de.zalando.domain.address.Address build() {
        normalize();

        if (!valid) {
            throw new IllegalStateException("builder already invalidated, please create a new one");
        }

        valid = false;

        return buildAddressImpl(new AddressImpl());
    }

    private void normalize() {
        if (noNormalization) {
            return;
        }

        this.city = normalizeCity(this.city);

        // trying to normalize (and guess) street and house number, if specified
        final Pair<String, String> streetAndHouseNumberGuess = normalizeStreetAndHouseNumber();

        // explicitly specified streetName gains higher precedence
        if (this.streetName != null) {
            this.streetName = normalizeStreetName(this.streetName);
        } else if (streetAndHouseNumberGuess != null) {
            this.streetName = streetAndHouseNumberGuess.getFirst();
        } else {
            this.streetName = "";
        }

        // explicitly specified houseNumber takes gains precedence
        if (this.houseNumber != null) {
            this.houseNumber = normalizeHouseNumber(this.houseNumber);
        } else if (streetAndHouseNumberGuess != null) {
            this.houseNumber = streetAndHouseNumberGuess.getSecond();
        } else {
            this.houseNumber = "";
        }

        this.streetAddition = normalizeStreetAddition(this.streetAddition);
        this.zip = normalizeZip(this.zip);
    }

    private String normalizeStreetName(String streetName) {
        if (streetName != null) {
            final Pair<String, String> normalizedStreet = processor.normalizeStreet(nullToEmpty(streetName));
            streetName = normalizedStreet.getFirst();
            this.streetAdditionGuess = Joiner.on(' ').useForNull("").join(this.streetAdditionGuess,
                    normalizedStreet.getSecond());
        } else {
            streetName = "";
        }

        return streetName;
    }

    private Pair<String, String> normalizeStreetAndHouseNumber() {
        final Pair<String, String> guess;
        if (this.streetWithHouseNumber != null) {
            final Pair<String, String> normalizedStreet = processor.normalizeStreet(nullToEmpty(
                        this.streetWithHouseNumber));

            logStreetNormalizationResult(streetWithHouseNumber, normalizedStreet);

            this.streetAdditionGuess = Joiner.on(' ').useForNull("").join(this.streetAdditionGuess,
                    normalizedStreet.getSecond());

            final AddressGuess addressGuess = processor.guessStreetNameAndNumber(normalizedStreet.getFirst());
            final String guessStreetName = addressGuess.getStreetName();
            final String guessHouseNumber = processor.normalizeHouseNumber(nullToEmpty(addressGuess.getHouseNumber()));

            guess = Pair.of(guessStreetName, guessHouseNumber);
        } else {
            guess = null;
        }

        return guess;
    }

    /**
     * Returns the constructed address. Throws an IllegalStateException, if already used.
     *
     * @return  address
     */
    public AddressWithDetails buildWithDetails() {
        if (!valid) {
            throw new IllegalStateException("builder already invalidated, please create a new one");
        }

        valid = false;

        final AddressWithDetailsImpl address = buildAddressWithDetails(new AddressWithDetailsImpl());
        buildAddressImpl(address);
        return address;
    }

    private AddressWithDetailsImpl buildAddressWithDetails(final AddressWithDetailsImpl address) {
        normalize();
        address.setSimilarity(similarity);
        address.setHouseNumberRange(houseNumberRange);
        return address;
    }

    private AddressImpl buildAddressImpl(final AddressImpl address) {

        address.setZip(zip);
        address.setCountryCode(countryCode);

        if (!this.wildcardCity) {
            address.setCity(city);
        }

        if (!wildcardHouseNumber && !isNullOrEmpty(houseNumber)) {
            address.setHouseNumber(houseNumber);
        }

        if (!wildcardStreet) {
            address.setStreetName(streetName);
            if (noNormalization) {
                address.setStreetWithNumber(this.streetWithHouseNumber);
            } else {
                address.setStreetWithNumber(processor.joinStreetWithHouseNumber(streetName, houseNumber));
            }

            address.setAdditional(emptyToNull(
                    String.format("%s %s", nullToEmpty(streetAddition).trim(),
                        nullToEmpty(streetAdditionGuess).trim()).trim()));

            mapTypeFields(address);
        }

        // remove control character from string
        removeInvalidCharacter(address);

        LOG.debug("built address {}", address);

        return address;
    }

    private void removeInvalidCharacter(final AddressImpl address) {
        address.setAdditional(removeInvalidChars(address.getAdditional()));
        address.setCity(removeInvalidChars(address.getCity()));
        address.setHouseNumber(removeInvalidChars(address.getHouseNumber()));
        address.setServicePoint(removeInvalidChars(address.getServicePoint()));
        address.setStreetName(removeInvalidChars(address.getStreetName()));
        address.setStreetWithNumber(removeInvalidChars(address.getStreetWithNumber()));
        address.setZip(removeInvalidChars(address.getZip()));
    }

    private String removeInvalidChars(final String riskyString) {
        if (Strings.isNullOrEmpty(riskyString)) {
            return riskyString;
        }

        final StringBuilder stringBuilder = new StringBuilder();
        for (final char c : riskyString.toCharArray()) {

            if (c < CHAR_SPACE) {

                // remove invalid control and space chars:
                if (VALID_CHAR_SPACE_SET.contains(c)) {
                    stringBuilder.append(c);
                }
            } else {
                stringBuilder.append(c);
            }
        }

        return stringBuilder.toString();
    }

    public String joinStreetWithHouseNumber(final String myStreetName, final String myHouseNumber) {
        return processor.joinStreetWithHouseNumber(myStreetName, myHouseNumber);
    }

    public AddressBuilder similarity(final Similarity mySimilarity) {
        this.similarity = mySimilarity;
        return this;
    }

    /**
     * Adds a street name.
     *
     * @param   streetName
     *
     * @return  current builder
     */
    public AddressBuilder streetName(final String streetName) {
        this.streetName = streetName;
        return this;
    }

    /**
     * Adds a house number.
     *
     * @param   houseNumber
     *
     * @return  current builder
     */
    public AddressBuilder houseNumber(final String houseNumber) {
        this.houseNumber = houseNumber;
        return this;
    }

    private String normalizeHouseNumber(final String houseNumber) {
        return processor.normalizeHouseNumber(nullToEmpty(houseNumber));
    }

    /**
     * Sets the range of house numbers in a street.
     *
     * @param   range
     *
     * @return
     */
    public AddressBuilder houseNumberRange(final Range range) {
        this.houseNumberRange = range;
        return this;
    }

    /**
     * Sets the range of house numbers in a street.
     *
     * @param   from
     * @param   to
     *
     * @return
     */
    public AddressBuilder houseNumberRange(final int from, final int to) {
        this.houseNumberRange = Range.of(from, to);
        return this;
    }

    /**
     * Adds a street with house number. The method tries to cut the value, and assign the splitted parts to the
     * respective attributes.
     *
     * @param   streetWithHouseNumber
     *
     * @return  current builder
     */
    public AddressBuilder streetWithHouseNumber(final String streetWithHouseNumber) {
        this.streetWithHouseNumber = streetWithHouseNumber;
        return this;
    }

    private void logStreetNormalizationResult(final String streetWithHouseNumber,
            final Pair<String, String> normalizedStreet) {
        if (normalizedStreet.getSecond() != null) {
            LOG.trace("'{}' normalized as '{}' as a street and house number, and '{}' as an additional part",
                new Object[] {streetWithHouseNumber, normalizedStreet.getFirst(), normalizedStreet.getSecond()});
        } else {
            LOG.trace("'{}' normalized as '{}' as a street and house number, and no additional part",
                new Object[] {streetWithHouseNumber, normalizedStreet.getFirst()});
        }
    }

    /**
     * Turns off normalization for this address.
     */
    public AddressBuilder noNormalization() {
        noNormalization = true;
        return this;
    }

    /**
     * Adds a street addition.
     *
     * @param   myStreetAddition
     *
     * @return  current builder
     */
    public AddressBuilder streetAddition(final String myStreetAddition) {
        this.streetAddition = myStreetAddition;
        return this;
    }

    private String normalizeStreetAddition(final String myStreetAddition) {
        return nullToEmpty(myStreetAddition);
    }

    /**
     * Adds a zip code.
     *
     * @param   myZip
     *
     * @return  current builder
     */
    public AddressBuilder zip(final String myZip) {
        this.zip = myZip;
        return this;
    }

    private String normalizeZip(final String myZip) {
        return processor.normalizeZip(nullToEmpty(myZip));
    }

    /**
     * Adds a city.
     *
     * @param   city
     *
     * @return  current builder
     */
    public AddressBuilder city(final String city) {
        this.city = city;
        return this;
    }

    private String normalizeCity(final String city) {
        final Pair<String, String> normalizeCity = processor.normalizeCity(nullToEmpty(city));
        final String result = normalizeCity.getFirst();
        this.streetAdditionGuess = Joiner.on(' ').useForNull("").join(this.streetAdditionGuess,
                normalizeCity.getSecond());
        return result;
    }

    /**
     * Map additional address fields depending on the type of address we're dealing with.
     *
     * @param  address
     */
    private static void mapTypeFields(final AddressImpl address) {
        switch (address.getType()) {

            case PACKSTATION :
                if (address.getAdditional() == null) {
                    return;
                }

                if (address.getAdditional().contains("Packstation")) {

                    // the customer number is the street with number field
                    address.setCustomerNumber(address.getStreetWithNumber());

                    // the station + number is in additional
                    address.setServicePoint(address.getAdditional());
                } else if (address.getStreetName().contains("Packstation")) {

                    // CASE 2 with exchanged lines from address change!
                    // the customer number is in additional
                    address.setCustomerNumber(address.getAdditional());

                    // the station + number is in streetnNumber
                    address.setServicePoint(address.getStreetWithNumber());
                }

                break;

            case KIALA :

                // The Kiala service point is mapped into the additional field,
                // remove
                // the linebreak separating the name and service point ID
                if (Strings.isNullOrEmpty(address.getServicePoint())) {
                    address.setServicePoint(StringUtils.remove(address.getAdditional(), AddressType.KIALA_PREFIX));
                }

                break;

            default :

                // Because checkstyle says I have to
                break;
        }
    }

    public AddressBuilder anyStreet() {

        this.streetName = null;
        this.streetAddition = null;

        this.wildcardStreet = true;
        return this;
    }

    public AddressBuilder anyHouseNumber() {

        this.houseNumber = null;
        this.houseNumberRange = null;

        this.wildcardHouseNumber = true;
        return this;
    }

    public AddressBuilder anyCity() {

        this.city = null;
        this.wildcardCity = true;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("AddressBuilder [processor=");
        builder.append(processor);
        builder.append(", streetName=");
        builder.append(streetName);
        builder.append(", streetAddition=");
        builder.append(streetAddition);
        builder.append(", streetAdditionGuess=");
        builder.append(streetAdditionGuess);
        builder.append(", streetWithHouseNumber=");
        builder.append(streetWithHouseNumber);
        builder.append(", houseNumber=");
        builder.append(houseNumber);
        builder.append(", city=");
        builder.append(city);
        builder.append(", zip=");
        builder.append(zip);
        builder.append(", noNormalization=");
        builder.append(noNormalization);
        builder.append(", countryCode=");
        builder.append(countryCode);
        builder.append(", similarity=");
        builder.append(similarity);
        builder.append(", valid=");
        builder.append(valid);
        builder.append(", houseNumberRange=");
        builder.append(houseNumberRange);
        builder.append(", wildcardStreet=");
        builder.append(wildcardStreet);
        builder.append(", wildcardHouseNumber=");
        builder.append(wildcardHouseNumber);
        builder.append(", wildcardCity=");
        builder.append(wildcardCity);
        builder.append(']');
        return builder.toString();
    }

}
