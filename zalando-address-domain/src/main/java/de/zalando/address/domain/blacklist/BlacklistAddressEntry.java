package de.zalando.address.domain.blacklist;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import de.zalando.address.domain.CustomerAddress;

@XmlType(propOrder = {"id", "active", "created", "address"})
public class BlacklistAddressEntry {

    private int id;

    private boolean active;

    private Date created;

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

    @XmlElement(name = "created", nillable = false, required = true)
    public Date getCreated() {
        return created;
    }

    public void setCreated(final Date created) {
        this.created = created;
    }

    @XmlElement(name = "address", nillable = false, required = true)
    public CustomerAddress getAddress() {
        return address;
    }

    public void setAddress(final CustomerAddress address) {
        this.address = address;
    }

}
