package de.zalando.address.domain.blacklist;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "blacklistReturnCode")
@XmlEnum
public enum BlacklistReturnCode {

    SAVED(true),
    UPDATED(true),
    ADDRESS_NOT_FOUND(false),
    ADDRESS_EXISTS(false),
    ERROR(false);

    private final boolean isSuccessful;

    private BlacklistReturnCode(final boolean returnCodePositive) {
        this.isSuccessful = returnCodePositive;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

}
