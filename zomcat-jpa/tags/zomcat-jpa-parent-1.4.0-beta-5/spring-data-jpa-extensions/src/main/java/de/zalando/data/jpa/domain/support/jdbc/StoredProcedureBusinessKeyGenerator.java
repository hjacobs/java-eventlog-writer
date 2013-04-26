package de.zalando.data.jpa.domain.support.jdbc;

import java.sql.Types;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;

import org.springframework.util.Assert;

import de.zalando.data.jpa.domain.support.BusinessKeyGenerator;

/**
 * Simple {@link StoredProcedure} object to 'get_next_number'.
 *
 * @author  jbellmann
 */
public class StoredProcedureBusinessKeyGenerator extends StoredProcedure implements BusinessKeyGenerator {

    static final String STORED_PROCEDURE_NAME = "zpu_data.get_next_number";
    static final String TYPE_NAME = "zpu_data.number_range_type";
    static final String IN_PARAMETER_NAME = "p_type";
    static final String OUT_PARAMETER_NAME = "number";
// static final Map<String, PGobject> PG_OBJECT_CACHE = Maps.newConcurrentMap();

    /**
     * Constructs the StoredProcedure.
     *
     * @param  dataSource  has to be not null
     */
    public StoredProcedureBusinessKeyGenerator(final DataSource dataSource) {
        Assert.notNull(dataSource, "DataSource should never be null");
        setDataSource(dataSource);
        setFunction(true);
        setSql(getStoredProcedureName());
        declareParameter(new SqlParameter(getInParameterName(), Types.OTHER));
        declareParameter(new SqlOutParameter(getOutParameterName(), Types.VARCHAR));
        compile();
    }

    public String getBusinessKeyForSelector(final String businessKeySelector) {
// PGobject pgObject = getPGobjectFromCache(businessKeySelector);
// Map<String, Object> input = Maps.newHashMap();
// input.put(getInParameterName(), pgObject);

        // execute
        Map<String, Object> resultMap = super.execute(businessKeySelector);
        return (String) resultMap.get(getOutParameterName());
    }

// protected PGobject getPGobjectFromCache(final String businessKeySelector) {
// PGobject result = PG_OBJECT_CACHE.get(businessKeySelector);
// if (result == null) {
// result = new PGobject();
// try {
// result.setValue(businessKeySelector);
// result.setType(getPgTypeName());
// } catch (SQLException e) {
// throw new RuntimeException(e);
// }
//
// PG_OBJECT_CACHE.put(businessKeySelector, result);
// }
//
// return result;
// }

    public String getStoredProcedureName() {
        return STORED_PROCEDURE_NAME;
    }

    public String getInParameterName() {
        return IN_PARAMETER_NAME;
    }

    public String getOutParameterName() {
        return OUT_PARAMETER_NAME;
    }

    public String getPgTypeName() {
        return TYPE_NAME;
    }
}
