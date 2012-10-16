package de.zalando.address.domain.completion;

import com.typemapper.annotations.DatabaseType;

@DatabaseType(name = "address_completion_status_type")
public enum AddressCompletionStatus {
    FAILURE,
    SUCCESS,
    ZIP_NOT_FOUND;
}
