package de.zalando.address.domain.blacklist;

import javax.xml.bind.annotation.XmlElement;

import de.zalando.address.domain.CustomerAddress;

public class BlacklistAddressRequest {

    private String refId;

    private CustomerAddress address;

    @XmlElement(required = true, nillable = false)
    public String getRefId() {
        return refId;
    }

    public void setRefId(final String refId) {
        this.refId = refId;
    }

    @XmlElement(required = true, nillable = false)
    public CustomerAddress getAddress() {
        return address;
    }

    public void setAddress(final CustomerAddress address) {
        this.address = address;
    }
}
