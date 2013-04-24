package de.zalando.typemapper.core.fieldMapper;

import org.apache.log4j.Logger;

public class PrimitiveLongFieldMapper implements FieldMapper {

    private static final Logger LOG = Logger.getLogger(PrimitiveLongFieldMapper.class);

    @Override
    public Object mapField(final String string, final Class clazz) {
        if (string == null) {
            return new Long(0);
        }

        try {
            return Long.parseLong(string);
        } catch (NumberFormatException e) {
            LOG.error("Could not convert " + string + " to long.");
        }

        return new Long(0);
    }

}
