package de.zalando.address.domain.blacklist;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import com.typemapper.annotations.DatabaseField;

@XmlType(propOrder = {"totalSize", "size", "addresses"})
public class BlacklistQueryResult {

    @DatabaseField(name = "totalcount")
    private int totalSize;

    @DatabaseField(name = "address_list")
    private List<BlacklistAddressEntry> addresses;

    @XmlElementWrapper(name = "blacklistedAddresses")
    @XmlElement(name = "blacklistEntry", nillable = false, required = true)
    public List<BlacklistAddressEntry> getAddresses() {
        return addresses;
    }

    public void setAddresses(final List<BlacklistAddressEntry> addresses) {
        this.addresses = addresses;
    }

    @XmlElement(name = "size", nillable = false, required = true)
    public int getSize() {
        return addresses.size();
    }

    @XmlElement(name = "totalSize", nillable = false, required = true)
    public int getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(final int totalSize) {
        this.totalSize = totalSize;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("BlacklistQueryResult [totalSize=");
        builder.append(totalSize);
        builder.append(", addresses=");
        builder.append(addresses);
        builder.append("]");
        return builder.toString();
    }
}
