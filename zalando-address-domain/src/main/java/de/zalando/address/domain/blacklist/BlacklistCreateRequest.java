package de.zalando.address.domain.blacklist;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import de.zalando.address.domain.CustomerAddress;

@XmlType(propOrder = {"active", "address"})
public class BlacklistCreateRequest {

    private boolean active;

    private CustomerAddress address;

    @XmlElement(name = "active", nillable = false, required = true)
    public boolean isActive() {
        return active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    @XmlElement(name = "address", nillable = false, required = true)
    public CustomerAddress getAddress() {
        return address;
    }

    public void setAddress(final CustomerAddress address) {
        this.address = address;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("BlacklistCreateRequest [active=");
        builder.append(active);
        builder.append(", address=");
        builder.append(address);
        builder.append("]");
        return builder.toString();
    }

}
