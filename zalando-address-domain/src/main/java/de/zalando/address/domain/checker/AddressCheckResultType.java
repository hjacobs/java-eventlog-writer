package de.zalando.address.domain.checker;

import com.typemapper.annotations.DatabaseType;

@DatabaseType(name = "address_check_result_type")
public enum AddressCheckResultType {

    /**
     * THIS ENUM IS PART OF TABLE address_check_type table zalando addr<br/>
     * DELETION OF VALUES IS STRICTLY FORBIDDEN.
     */
    NOT_FOUND,
    NOT_CORRECT,
    CORRECT,
    NORMALIZED
}
