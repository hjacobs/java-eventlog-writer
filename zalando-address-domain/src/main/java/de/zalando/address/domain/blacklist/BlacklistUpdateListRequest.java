package de.zalando.address.domain.blacklist;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

public class BlacklistUpdateListRequest {

    private List<BlacklistUpdateRequest> updateEntries;

    private static final List<BlacklistUpdateRequest> EMPTY = new ArrayList<BlacklistUpdateRequest>();

    @XmlElementWrapper(name = "updates")
    @XmlElement(name = "updateEntry", required = true, nillable = false)
    public List<BlacklistUpdateRequest> getUpdateEntries() {
        if (updateEntries == null) {
            return EMPTY;
        }

        return updateEntries;
    }

    public void setUpdateEntries(final List<BlacklistUpdateRequest> updateEntries) {
        this.updateEntries = updateEntries;
    }
}
