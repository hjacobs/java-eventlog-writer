package de.zalando.address.domain.checker;

import com.typemapper.annotations.DatabaseField;

public class AddressRoutingCodeResult {

    @DatabaseField(name = "routing_code")
    private String addressRoutingCode;

    private AddressRoutingCodeStatus status;

    public String getAddressRoutingCode() {
        return addressRoutingCode;
    }

    public void setAddressRoutingCode(final String addressRoutingCode) {
        this.addressRoutingCode = addressRoutingCode;
    }

    public AddressRoutingCodeStatus getStatus() {
        return status;
    }

    public void setStatus(final AddressRoutingCodeStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("AddressRoutingCodeResult [addressRoutingCode=");
        builder.append(addressRoutingCode);
        builder.append(", status=");
        builder.append(status);
        builder.append("]");
        return builder.toString();
    }

}
