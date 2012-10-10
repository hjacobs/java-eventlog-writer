package de.zalando.address.domain.blacklist;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {"street", "zip", "size", "offset"})
public class BlacklistAddressSearchRequest implements Serializable {

    private static final long serialVersionUID = 3074610179082999230L;

    private String street;

    private String zip;

    private int size;

    private int offset;

    public BlacklistAddressSearchRequest() {
        super();
    }

    /**
     * @param  street
     * @param  zip
     * @param  size
     * @param  offset
     */
    public BlacklistAddressSearchRequest(final String street, final String zip, final int size, final int offset) {
        super();
        this.street = street;
        this.zip = zip;
        this.size = size;
        this.offset = offset;
    }

    /**
     * @return  the street
     */
    @XmlElement(name = "street", nillable = false, required = true)
    public String getStreet() {
        return street;
    }

    /**
     * @param  street  the street to set
     */
    public void setStreet(final String street) {
        this.street = street;
    }

    /**
     * @return  the zip
     */
    @XmlElement(name = "zip", nillable = false, required = true)
    public String getZip() {
        return zip;
    }

    /**
     * @param  zip  the zip to set
     */
    public void setZip(final String zip) {
        this.zip = zip;
    }

    /**
     * @return  the size
     */
    @XmlElement(name = "size", nillable = false, required = true)
    public int getSize() {
        return size;
    }

    /**
     * @param  size  the size to set
     */
    public void setSize(final int size) {
        this.size = size;
    }

    /**
     * @return  the offset
     */
    @XmlElement(name = "offset", nillable = false, required = true)
    public int getOffset() {
        return offset;
    }

    /**
     * @param  offset  the offset to set
     */
    public void setOffset(final int offset) {
        this.offset = offset;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("GetAddressesRequest [street=");
        builder.append(street);
        builder.append(", zip=");
        builder.append(zip);
        builder.append(", size=");
        builder.append(size);
        builder.append(", offset=");
        builder.append(offset);
        builder.append("]");
        return builder.toString();
    }

}
