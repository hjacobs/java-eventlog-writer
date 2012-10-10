package de.zalando.address.domain.completion;

import com.typemapper.annotations.DatabaseField;

public class AddressCompletion {

    @DatabaseField
    private String zip;

    @DatabaseField
    private String city;

    @DatabaseField
    private String streetName;

    public String getZip() {
        return zip;
    }

    public void setZip(final String zip) {
        this.zip = zip;
    }

    public String getCity() {
        return city;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(final String streetName) {
        this.streetName = streetName;
    }
}
