package de.zalando.catalog.domain.multimedia.adapter;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.sessions.Session;

import de.zalando.catalog.domain.multimedia.MultimediaTypeCode;

public class MultimediaTypeCodeConverter implements Converter {

    @Override
    public Object convertObjectValueToDataValue(final Object objectValue, final Session session) {
        if (objectValue == null) {
            return null;
        }

        return ((MultimediaTypeCode) objectValue).getCode();
    }

    @Override
    public Object convertDataValueToObjectValue(final Object dataValue, final Session session) {
        if (dataValue == null) {
            return null;
        }

        return new MultimediaTypeCode((String) dataValue);
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public void initialize(final DatabaseMapping mapping, final Session session) { }
}
