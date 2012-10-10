package de.zalando.address.domain.util.builder;

public class AddressGuess {

    private final String streetName;

    private final String houseNumber;

    public AddressGuess(final String streetName, final String houseNumber) {
        this.streetName = streetName;
        this.houseNumber = houseNumber;
    }

    public String getStreetName() {
        return streetName;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AddressGuess [streetName=");
        builder.append(streetName);
        builder.append(", houseNumber=");
        builder.append(houseNumber);
        builder.append(']');
        return builder.toString();
    }
}
