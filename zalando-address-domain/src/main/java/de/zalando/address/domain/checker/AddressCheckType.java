package de.zalando.address.domain.checker;

import com.typemapper.annotations.DatabaseType;

// TODO: will be refactored - moving from BM DB to addr DB and table will become DB Enum Type
@DatabaseType(name = "address_check_type")
public enum AddressCheckType {

    /**
     * THIS ENUM IS PART OF TABLE address_check_type table zalando addr<br/>
     * DELETION OF VALUES IS STRICTLY FORBIDDEN.
     */
    DATA_FACTORY_STREET_VIEW,
    GOOGLE_MAPS,
    EOS,
    STORED,
    OPEN_STREET_MAP;

// private int code;

// private AddressCheckType(final int code) {
// this.code = code;
// }

// public int getCode() {
// return code;
// }

}
