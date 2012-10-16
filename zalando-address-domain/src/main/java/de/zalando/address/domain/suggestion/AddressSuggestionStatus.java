package de.zalando.address.domain.suggestion;

import com.typemapper.annotations.DatabaseType;

/**
 * Possible result status for AddressSuggestions.
 *
 * @author         john
 * @formatter:off  SUCCESS, -- normal successful operation (loading all results) SUCCESS_LIMITED, -- successful
 *                 operation; more than the max allowed result set were found and the result set is limited.
 *                 ZIP_NOT_FOUND, -- given zip code (or prefix) found no matches on database. This has roughly the same
 *                 semantics of StatusMessage.ZIP_NOT_FOUND (604). INVALID_INPUT -- input parameters (i.e. zip less than
 *                 3 characters) FAILURE -- any other unspecified error.
 * @formatter:on
 */
@DatabaseType(name = "address_suggestion_status_type")
public enum AddressSuggestionStatus {

    SUCCESS,
    SUCCESS_LIMITED,
    ZIP_NOT_FOUND,
    INVALID_INPUT,
    FAILURE,
    SUCCESS_EMPTY

}
