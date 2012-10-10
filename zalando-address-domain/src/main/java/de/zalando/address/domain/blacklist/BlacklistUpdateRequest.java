package de.zalando.address.domain.blacklist;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import de.zalando.address.domain.CustomerAddress;

@XmlType(propOrder = {"id", "active", "address"})
public class BlacklistUpdateRequest {

    private int id;

    private boolean active;

    private CustomerAddress address;

    @XmlElement(name = "id", nillable = false, required = true)
    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    @XmlElement(name = "active", nillable = false, required = true)
    public boolean isActive() {
        return active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    public CustomerAddress getAddress() {
        return address;
    }

    public void setAddress(final CustomerAddress address) {
        this.address = address;
    }

}
