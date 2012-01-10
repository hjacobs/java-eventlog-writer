package de.zalando.sprocwrapper;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.zalando.sprocwrapper.example.ExampleDomainObject;
import de.zalando.sprocwrapper.example.ExampleSProcService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:backendContextTest.xml"})
public class SimpleIT {

    @Autowired
    private ExampleSProcService exampleSProcService;

    @Test
    public void testSample() throws SQLException {

        // test void result
        exampleSProcService.getSimpleIntIgnore();

        assertEquals(3, (int) exampleSProcService.getSimpleInt());
        assertEquals(3, exampleSProcService.getSimpleIntAsPrimitive());
        exampleSProcService.createArticleSimpleItems("sku", 1, 12, 13, "1001");
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
}
