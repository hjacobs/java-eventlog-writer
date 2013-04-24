package de.zalando.typemapper.core.fieldMapper;

import de.zalando.typemapper.postgres.HStore;

public class HStoreFieldMapper implements FieldMapper {

    // private static final Logger LOG = Logger.getLogger(HStoreFieldMapper.class);

    @Override
    public Object mapField(final String value, final Class clazz) {
        if (value == null) {
            return null;
        }

        HStore hstore = new HStore(value);
        return hstore.asMap();
    }

}
