package de.zalando.sprocwrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Ignore;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.zalando.sprocwrapper.example.ExampleDomainObject;
import de.zalando.sprocwrapper.example.ExampleDomainObjectWithInnerObject;
import de.zalando.sprocwrapper.example.ExampleDomainObjectWithMap;
import de.zalando.sprocwrapper.example.ExampleEnum;
import de.zalando.sprocwrapper.example.ExampleSProcService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:backendContextTest.xml"})
public class SimpleIT {

    @Autowired
    private ExampleSProcService exampleSProcService;

    @Autowired
    @Qualifier("testDataSource1")
    private DataSource dataSource1;

    @Test
    public void testSample() throws SQLException {

        // test void result
        exampleSProcService.getSimpleIntVoid(1);

        assertEquals(3, (int) exampleSProcService.getSimpleInt());
        assertEquals(3, exampleSProcService.getSimpleIntAsPrimitive());
        exampleSProcService.createArticleSimpleItems("sku", 1, 12, 13, "1001");

        assertEquals(true, exampleSProcService.getBoolean());

        exampleSProcService.setBoolean(true);
    }

    @Test
    public void testSimpleListParam() throws SQLException {

        List<String> skus = new ArrayList<String>();
        skus.add("ABC123");
        skus.add("ABC456");

        exampleSProcService.createArticleSimples(skus);
    }

    @Test
    public void testMultiRowTypeMappedResult() {

        // Query for a Multi Row Resultset of TestResult Objects
        List<ExampleDomainObject> rows = exampleSProcService.getResult();
        assertEquals("a", rows.get(0).getA());
        assertEquals("b", rows.get(0).getB());
        assertEquals("c", rows.get(1).getA());
        assertEquals("d", rows.get(1).getB());
    }

    @Test
    public void testParameterOverloading() {
        assertEquals(3, (int) exampleSProcService.getSimpleInt());
        assertEquals(1234, exampleSProcService.getSimpleInt(1234));
    }

    @Test
    public void testSharding() {

        // test simple identity + modulo sharding strategy
        assertEquals(0, exampleSProcService.getShardIndex(122));
        assertEquals(1, exampleSProcService.getShardIndex(123));
    }

    @Test
    public void testObjectParam() {

        String result = exampleSProcService.createOrUpdateObject(null);
        assertEquals(null, result);

        ExampleDomainObject obj = new ExampleDomainObject("a", "b");
        result = exampleSProcService.createOrUpdateObject(obj);
        assertEquals("a b", result);
    }

    @Test
    public void testListParam() {

        String result = exampleSProcService.createOrUpdateMultipleObjects(null);
        assertEquals("", result);

        result = exampleSProcService.createOrUpdateMultipleObjects(new ArrayList<ExampleDomainObject>());
        assertEquals("", result);

        ExampleDomainObject obj = new ExampleDomainObject("a", "b");
        List<ExampleDomainObject> list = new ArrayList<ExampleDomainObject>();
        list.add(obj);
        list.add(new ExampleDomainObject("c", "d"));

        result = exampleSProcService.createOrUpdateMultipleObjects(list);
        assertEquals("a_b,c_d,", result);
    }

    @Test
    public void testListParamWithMap() {

        String result = exampleSProcService.createOrUpdateMultipleObjectsWithMap(null);
        assertNull(result);

        result = exampleSProcService.createOrUpdateMultipleObjectsWithMap(new ArrayList<ExampleDomainObjectWithMap>());
        assertNull(result);

        ExampleDomainObjectWithMap obj = new ExampleDomainObjectWithMap("a", null);
        List<ExampleDomainObjectWithMap> list = new ArrayList<ExampleDomainObjectWithMap>();
        list.add(obj);
        list.add(new ExampleDomainObjectWithMap("c", new HashMap<String, String>()));
        list.get(1).b.put("key", "val");

        result = exampleSProcService.createOrUpdateMultipleObjectsWithMap(list);
        assertEquals("<c_key_val>", result);

        list.get(0).b = new HashMap<String, String>();

        result = exampleSProcService.createOrUpdateMultipleObjectsWithMap(list);
        assertEquals("<a__>,<c_key_val>", result);

        // test void result
        exampleSProcService.createOrUpdateMultipleObjectsWithMapVoid(list);
    }

    @Test
    public void textComplexParam() {

        String result = exampleSProcService.createOrUpdateMultipleObjectsWithInnerObject(null);
        assertNull(result);

        result = exampleSProcService.createOrUpdateMultipleObjectsWithInnerObject(
                new ArrayList<ExampleDomainObjectWithInnerObject>());
        assertNull(result);

        ExampleDomainObjectWithInnerObject obj = new ExampleDomainObjectWithInnerObject("a", null);
        List<ExampleDomainObjectWithInnerObject> list = new ArrayList<ExampleDomainObjectWithInnerObject>();
        list.add(obj);
        list.add(new ExampleDomainObjectWithInnerObject("c", new ExampleDomainObject("d", "e")));

        result = exampleSProcService.createOrUpdateMultipleObjectsWithInnerObject(list);
        assertEquals("<c_d|e>", result);

        obj.setC(new ArrayList<ExampleDomainObject>());
        obj.getC().add(new ExampleDomainObject("f", "g"));
        result = exampleSProcService.createOrUpdateMultipleObjectsWithInnerObject(list);
        assertEquals("<c_d|e>", result);
    }

    @Test
    public void testEnum() {
        exampleSProcService.useEnumParam(ExampleEnum.ENUM_CONST_1);
    }

    @Test
    @Ignore("performance test only")
    public void testRuntime() {
        assertEquals(1, 1);

        int loops = 10000;

        String sql = "SELECT ";

        int xx = (new JdbcTemplate(dataSource1)).queryForInt(sql + 11111);

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < loops; i++) {
            int j = (new JdbcTemplate(dataSource1)).queryForInt(sql + i);
        }

        long endTime = System.currentTimeMillis();

        long startTimeW = System.currentTimeMillis();
        for (int i = 0; i < loops; i++) {
            int j = exampleSProcService.getSimpleInt(i);
        }

        long endTimeW = System.currentTimeMillis();

        long startTimeN = System.currentTimeMillis();

        for (int i = 0; i < loops; i++) {
            Connection conn = null;
            try {
                conn = dataSource1.getConnection();

                Statement st = conn.createStatement();

                int j = 0;

                ResultSet rs = st.executeQuery("SELECT " + i);

                if (rs.next()) {
                    j = rs.getInt(1);
                }

            } catch (SQLException e) { }
            finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException e) { }
                }
            }
        }

        long endTimeN = System.currentTimeMillis();

        System.out.println("Time used for native JdbcTemplate: " + (endTime - startTime));
        System.out.println("Time used for SprocWrapper: " + (endTimeW - startTimeW));
        System.out.println("Time used for Native: " + (endTimeN - startTimeN));
    }
}
