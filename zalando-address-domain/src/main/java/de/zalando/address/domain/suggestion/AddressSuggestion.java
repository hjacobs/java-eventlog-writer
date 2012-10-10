package de.zalando.address.domain.suggestion;

import com.typemapper.annotations.DatabaseField;

public class AddressSuggestion {

    @DatabaseField(name = "zip")
    private String zip;

    @DatabaseField(name = "city")
    private String city;

    @DatabaseField(name = "street_name")
    private String street1;

    private String street2;

    @DatabaseField(name = "house_number")
    private String houseNumber;

    public String getCity() {
        return city;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(final String zip) {
        this.zip = zip;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(final String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getStreet1() {
        return street1;
    }

    public void setStreet1(final String street1) {
        this.street1 = street1;
    }

    public String getStreet2() {
        return street2;
    }

    public void setStreet2(final String street2) {
        this.street2 = street2;
    }

    @Override
    public String toString() {
        return "AddressSuggestion [zip=" + zip + ", city=" + city + ", street1=" + street1 + ", street2=" + street2
                + ", houseNumber=" + houseNumber + ']';
    }

    public void splitLongStreet(final int limit) {
        if (street1 == null) {
            street2 = null;
            return;
        }

        if (street1.length() > limit) {
            final int pos = street1.indexOf(", ");
            street2 = street1.substring(pos + 1).trim(); // +2 since we ignore ", "
            street1 = street1.substring(0, pos).trim();
        }

    }

}
