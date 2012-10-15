package de.zalando.address.domain.checker;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {"refId", "blacklisted", "status", "address", "suggestedAddresses"})
public class AddressCheckResult {

    private String refId;
    private boolean blacklisted;
    private AddressCheckResultType status;
    private CheckedDeliveryAddress address;
    private List<CheckedDeliveryAddress> suggestedAddresses;

    public String getRefId() {
        return refId;
    }

    public void setRefId(final String refId) {
        this.refId = refId;
    }

    public AddressCheckResultType getStatus() {
        return status;
    }

    public void setStatus(final AddressCheckResultType status) {
        this.status = status;
    }

    public CheckedDeliveryAddress getAddress() {
        return address;
    }

    public void setAddress(final CheckedDeliveryAddress address) {
        this.address = address;
    }

    public boolean isBlacklisted() {
        return blacklisted;
    }

    public void setBlacklisted(final boolean blacklisted) {
        this.blacklisted = blacklisted;
    }

    @XmlElementWrapper(name = "suggestedAddresses")
    @XmlElement(name = "address")
    public List<CheckedDeliveryAddress> getSuggestedAddresses() {
        return suggestedAddresses;
    }

    public void setSuggestedAddresses(final List<CheckedDeliveryAddress> suggestedAddresses) {
        this.suggestedAddresses = suggestedAddresses;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("AddressCheckResult [refId=");
        builder.append(refId);
        builder.append(", blacklisted=");
        builder.append(blacklisted);
        builder.append(", status=");
        builder.append(status);
        builder.append(", address=");
        builder.append(address);
        builder.append(", suggestedAddresses=");
        builder.append(suggestedAddresses);
        builder.append("]");
        return builder.toString();
    }

}
