package de.zalando.address.domain.blacklist;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

public class BlacklistAddressListRequest {

    private List<BlacklistCreateRequest> createEntries;

    private static final List<BlacklistCreateRequest> EMPTY = new ArrayList<BlacklistCreateRequest>();

    @XmlElementWrapper(name = "creates")
    @XmlElement(name = "createEntry", required = true, nillable = false)
    public List<BlacklistCreateRequest> getCreateEntries() {
        if (createEntries == null) {
            return EMPTY;
        }

        return createEntries;
    }

    public void setCreateEntries(final List<BlacklistCreateRequest> createEntries) {
        this.createEntries = createEntries;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("BlacklistAddressListRequest [createEntries=");
        builder.append(createEntries);
        builder.append("]");
        return builder.toString();
    }
}
