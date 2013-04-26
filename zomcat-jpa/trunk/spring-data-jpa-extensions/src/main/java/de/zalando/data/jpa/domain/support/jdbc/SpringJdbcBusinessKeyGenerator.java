package de.zalando.data.jpa.domain.support.jdbc;

import java.sql.Types;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;

import org.springframework.util.Assert;

import com.google.common.collect.Maps;

import de.zalando.data.jpa.domain.support.BusinessKeyGenerator;

/**
 * Eine alternative zur SprocService-Implementierung. Klein, einfach und gut zu verstehen.
 *
 * @author  jbellmann
 */
public class SpringJdbcBusinessKeyGenerator implements BusinessKeyGenerator {

/*    @Autowired
 *  @Resource(name = "dataSourceFrontendSProc")*/
    private DataSource dataSource;

    private NextNumberStoredProcedure procedure;

// @PostConstruct
    public void init() {
        Assert.notNull(dataSource, "DataSource should never be null");
        procedure = new NextNumberStoredProcedure(dataSource);

        // TODO koennen wir hier je start eine Nummer verbraten? oder muss das hier lueckenlos sein?
        // if this test is fails, something went wrong and system should halt until fixed
        // String initialTest = procedure.nextNumber(NumberRangeType.PURCHASE_ORDER);
        // Assert.notNull(initialTest, "Init-Test failed");
    }

    @Override
    public String getBusinessKeyForSelector(final String businessKeySelector) {
        Assert.notNull(businessKeySelector, "BusinessKeySelector should not be null");
        return procedure.nextNumber(businessKeySelector);
    }

    /**
     * Simple {@link StoredProcedure} object to 'get_next_number'.
     *
     * @author  jbellmann
     */
    private static class NextNumberStoredProcedure extends StoredProcedure {

        static final String STORED_PROCEDURE_NAME = "zpu_data.get_next_number";
        static final String TYPE_NAME = "zpu_data.number_range_type";
        static final String IN_PARAMETER_NAME = "p_type";
        static final String OUT_PARAMETER_NAME = "number";
// static final Map<String, PGobject> PG_OBJECT_CACHE = Maps.newHashMap();

        NextNumberStoredProcedure(final DataSource dataSource) {
            setDataSource(dataSource);
            setFunction(true);
            setSql(STORED_PROCEDURE_NAME);
            declareParameter(new SqlParameter(IN_PARAMETER_NAME, Types.OTHER));
            declareParameter(new SqlOutParameter(OUT_PARAMETER_NAME, Types.VARCHAR));
            compile();
        }

        String nextNumber(final String numberRangeType) {
            Map<String, Object> input = Maps.newHashMap();
// input.put(IN_PARAMETER_NAME, PG_OBJECT_CACHE.get(numberRangeType));

            Map<String, Object> resultMap = execute(numberRangeType);
            return (String) resultMap.get(OUT_PARAMETER_NAME);
        }
    }

}
