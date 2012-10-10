package de.zalando.address.domain.util.builder.processor;

import de.zalando.address.domain.util.builder.AddressGuess;
import de.zalando.address.domain.util.builder.NumberPosition;

import de.zalando.utils.Pair;

public class DefaultAddressProcessor implements AddressProcessor {

    @Override
    public AddressGuess guessStreetNameAndNumber(final String street) {
        return new AddressGuess(street, "");
    }

    @Override
    public Pair<String, String> normalizeStreet(final String street) {
        if (street == null) {
            return Pair.of(null, null);
        }

        return Pair.of(street.trim(), null);
    }

    @Override
    public String normalizeZip(final String zip) {
        if (zip == null) {
            return zip;
        }

        return zip.trim();
    }

    @Override
    public Pair<String, String> normalizeCity(final String city) {

        if (city == null) {
            return Pair.of(null, null);
        }

        return Pair.of(city.trim(), null);
    }

    @Override
    public String normalizeCountryCode(final String countryCode) {
        if (countryCode == null) {
            return countryCode;
        }

        return countryCode.trim();
    }

    @Override
    public String joinStreetWithHouseNumber(final String streetName, final String houseNumber) {
        return String.format("%s %s", streetName, houseNumber).trim();
    }

    @Override
    public NumberPosition getNumberPosition() {
        return NumberPosition.RIGHT;
    }

    @Override
    public String normalizeHouseNumber(final String houseNumber) {
        if (houseNumber == null) {
            return houseNumber;
        }

        return houseNumber.trim();
    }
}
