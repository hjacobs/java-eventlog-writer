package de.zalando.address.domain.util.builder.processor;

import de.zalando.address.domain.util.builder.AddressGuess;
import de.zalando.address.domain.util.builder.NumberPosition;

import de.zalando.utils.Pair;

/**
 * An AddressProcessor is a class, that tries to handle various aspects of addresses. For example guessing the house
 * number. Each AddressProcessor is country specific.
 *
 * @author  Tamas.Eppel@Zalando.de
 */
public interface AddressProcessor {

    /**
     * This method tries to guess the street number in the address string. On error it returns an empty string.
     *
     * @param   address
     *
     * @return  guess for the street number
     */
    AddressGuess guessStreetNameAndNumber(final String street);

    /**
     * Tries to split street line into street name and an additional part.
     *
     * @param   street
     *
     * @return  pair of street name and additional part (if present)
     */
    Pair<String, String> normalizeStreet(final String street);

    String normalizeHouseNumber(final String houseNumber);

    String normalizeZip(final String zip);

    Pair<String, String> normalizeCity(final String city);

    String normalizeCountryCode(final String countryCode);

    String joinStreetWithHouseNumber(final String streetName, final String houseNumber);

    NumberPosition getNumberPosition();

}
