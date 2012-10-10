package de.zalando.address.domain.blacklist;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

@XmlType
public class BlacklistSaveResult {

    private List<BlacklistReturnCode> blacklistReturnCodes = new ArrayList<BlacklistReturnCode>();

    public BlacklistSaveResult() { }

    public BlacklistSaveResult(final List<BlacklistReturnCode> blacklistReturnCodes) {
        this.blacklistReturnCodes = blacklistReturnCodes;
    }

    @XmlElementWrapper(name = "blacklistReturnCodes")
    @XmlElement(name = "entry", nillable = false, required = true)
    public List<BlacklistReturnCode> getReturnCodes() {
        return blacklistReturnCodes;
    }

}
