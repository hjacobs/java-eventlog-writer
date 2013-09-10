package de.zalando.catalog.domain.multimedia.adapter;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.sessions.Session;

import de.zalando.catalog.domain.multimedia.MediaCharacterCode;

public class MediaCharacterCodeConverter implements Converter {

    @Override
    public Object convertObjectValueToDataValue(final Object objectValue, final Session session) {
        if (objectValue == null) {
            return null;
        }

        return ((MediaCharacterCode) objectValue).getCode();
    }

    @Override
    public Object convertDataValueToObjectValue(final Object dataValue, final Session session) {
        if (dataValue == null) {
            return null;
        }

        return new MediaCharacterCode((String) dataValue);
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public void initialize(final DatabaseMapping mapping, final Session session) { }
}
