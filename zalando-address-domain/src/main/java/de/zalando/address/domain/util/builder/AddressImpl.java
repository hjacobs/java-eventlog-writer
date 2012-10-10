package de.zalando.address.domain.util.builder;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Joiner;

import de.zalando.domain.address.Address;
import de.zalando.domain.address.AddressType;
import de.zalando.domain.globalization.ISOCountryCode;

/**
 * ATTENTION if you change this class then change {@link GsonAddressAdapter GsonAddressAdapter} as well.
 */
public class AddressImpl implements Address {

    private String streetName;
    private String houseNumber;
    private String streetWithNumber;
    private String additional;
    private String city;
    private String zip;
    private ISOCountryCode countryCode;
    private String addressString;
    private String servicePoint;   // eg. Packstation No. / Kiala Service Point Id
    private String customerNumber; // eg. Packstation Customer No.

    AddressImpl() {
        super();
    }

    @Override
    public String getStreetWithNumber() {
        return streetWithNumber;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.zalando.bm.backend.domain.address.AddressInt#getStreetName()
     */
    @Override
    public String getStreetName() {
        return streetName;
    }

    /**
     * Sets the name of the street.
     *
     * @param  streetName
     */
    void setStreetName(final String streetName) {
        this.streetName = streetName;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.zalando.bm.backend.domain.address.AddressInt#getHouseNumber()
     */
    @Override
    public String getHouseNumber() {
        return houseNumber;
    }

    void setHouseNumber(final String houseNumber) {
        this.houseNumber = houseNumber;
    }

    void setStreetWithNumber(final String streetWithNumber) {
        this.streetWithNumber = streetWithNumber;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.zalando.bm.backend.domain.address.AddressInt#getCity()
     */
    @Override
    public String getCity() {
        return city;
    }

    /**
     * Sets the city. The argument will be trimmed. Multiple whitespace is compacted into one space.
     *
     * @param  city
     */
    final void setCity(final String city) {
        this.city = city;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.zalando.bm.backend.domain.address.AddressInt#getZip()
     */
    @Override
    public String getZip() {
        return zip;
    }

    /**
     * Sets the zip code. The argument will be trimmed. Multiple whitespace is compacted into one space.
     *
     * @param  zip
     */
    final void setZip(final String zip) {
        this.zip = zip;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.zalando.bm.backend.domain.address.AddressInt#getCountryCode()
     */
    @Override
    public ISOCountryCode getCountryCode() {
        return countryCode;
    }

    /**
     * Sets the country code. The argument will be trimmed. Multiple whitespace is compacted into one space.
     *
     * @param  countryCode
     */
    final void setCountryCode(final ISOCountryCode countryCode) {
        this.countryCode = countryCode;
    }

    @Override
    public String getAdditional() {
        return additional;
    }

    final void setAdditional(final String aditional) {
        this.additional = aditional;
    }

    @Override
    public String getServicePoint() {
        return servicePoint;
    }

    final void setServicePoint(final String servicePoint) {
        this.servicePoint = servicePoint;
    }

    @Override
    public String getCustomerNumber() {
        return customerNumber;
    }

    final void setCustomerNumber(final String customerNumber) {
        this.customerNumber = customerNumber;
    }

    @Override
    public String toString() {
        return getAddressString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result;
        if (getAddressString() != null) {
            result += getAddressString().hashCode();
        }

        return result;
    }

    @Override
    public boolean equals(final Object obj) {

        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (!(obj instanceof AddressImpl)) {
            return false;
        }

        final AddressImpl other = (AddressImpl) obj;

        if (getAddressString() == null) {
            if (other.getAddressString() != null) {
                return false;
            }
        } else if (!getAddressString().equals(other.getAddressString())) {
            return false;
        }

        return true;
    }

    @Override
    public AddressType getType() {

        if (ISOCountryCode.DE.equals(countryCode)
                && (StringUtils.contains(additional, "Packstation") || StringUtils.contains(streetName, "Packstation")
                    || (servicePoint != null))) {
            return AddressType.PACKSTATION;
        } else if (ISOCountryCode.FR.equals(countryCode)
                && ((servicePoint != null) || StringUtils.contains(additional, AddressType.KIALA_PREFIX))) {
            return AddressType.KIALA;

        }

        return AddressType.DEFAULT;
    }

    private static final Joiner FIELD_JOINER = Joiner.on(", ").skipNulls();

    // helper to generate the address string
    private String getAddressString() {
        if (addressString == null) {
            addressString = FIELD_JOINER.join(streetWithNumber, additional, zip, city, countryCode);
        }

        return addressString;
    }
}
