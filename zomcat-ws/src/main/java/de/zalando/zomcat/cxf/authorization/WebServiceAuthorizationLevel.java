package de.zalando.zomcat.cxf.authorization;

import com.google.common.base.Strings;

public enum WebServiceAuthorizationLevel {

    ENABLED,
    DISABLED,
    LOGGING_MODE;

    public static WebServiceAuthorizationLevel getEnum(final String value) {
        if (Strings.isNullOrEmpty(value)) {
            return null;
        }

        return valueOf(value.toUpperCase());
    }

}
