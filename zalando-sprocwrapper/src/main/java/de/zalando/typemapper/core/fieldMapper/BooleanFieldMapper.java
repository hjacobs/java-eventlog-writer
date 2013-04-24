package de.zalando.typemapper.core.fieldMapper;

import org.apache.log4j.Logger;

import de.zalando.typemapper.parser.exception.ParserException;
import de.zalando.typemapper.parser.postgres.ParseUtils;

public class BooleanFieldMapper implements FieldMapper {

    private static final Logger LOG = Logger.getLogger(CharFieldMapper.class);

    @Override
    public Object mapField(final String string, final Class clazz) {
        if (string == null) {
            return null;
        }

        try {
            return ParseUtils.getBoolean(string);
        } catch (ParserException e) {
            LOG.error("Could not convert " + string + " to Boolean.");
            return null;
        }
    }

}
