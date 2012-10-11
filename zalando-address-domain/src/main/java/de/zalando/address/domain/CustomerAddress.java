package de.zalando.address.domain;

import com.typemapper.annotations.DatabaseField;

import de.zalando.domain.address.Address;
import de.zalando.domain.address.AddressType;
import de.zalando.domain.globalization.ISOCountryCode;

public class CustomerAddress implements Address {
    @DatabaseField
    protected String streetName;
    @DatabaseField
    protected String houseNumber;
    protected String streetWithNumber;
    @DatabaseField
    protected String city;
    @DatabaseField
    protected String zip;
    protected String additional;
    @DatabaseField
    protected ISOCountryCode countryCode;

    public CustomerAddress() {
        super();
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(final String streetName) {
        this.streetName = streetName;
    }

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

    public String getAdditional() {
        return additional;
    }

    public void setAdditional(final String additional) {
        this.additional = additional;
    }

    public ISOCountryCode getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(final ISOCountryCode countryCode) {
        this.countryCode = countryCode;
    }

    @Override
    public String getStreetWithNumber() {
        return streetWithNumber;
    }

    @Override
    public String getHouseNumber() {

        return houseNumber;
    }

    public void setHouseNumber(final String houseNumber) {

        this.houseNumber = houseNumber;
    }

    @Override
    public String getServicePoint() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getCustomerNumber() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public AddressType getType() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
