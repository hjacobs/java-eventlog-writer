package de.zalando.sprocwrapper.util;

import org.junit.Assert;
import org.junit.Test;

import de.zalando.typemapper.postgres.PgTypeHelper;

public class NameUtilsTest {

    @Test
    public void testCamelCaseToUnderScore() {
        Assert.assertEquals("test_camel_case_to_under_score",
            NameUtils.camelCaseToUnderscore("testCamelCaseToUnderScore"));
    }

    @Test
    public void testCamelCaseToUnderScoreWithUpperWord() {
        Assert.assertEquals("test_camel_case_to_under_score",
            NameUtils.camelCaseToUnderscore("testCAMELCaseToUnderScore"));
    }

    @Test
    public void testUpperCamelCaseToUnderScore() {
        Assert.assertEquals("http_servlet", NameUtils.camelCaseToUnderscore("HTTPServlet"));
    }

    @Test
    public void testUpperCamelCaseToUnderScoreTypemapper() {

        // different behavior in typemapper. This test exists just to notify the differences between both algorithms
        Assert.assertEquals("htt_pservlet", PgTypeHelper.camelCaseToUnderScore("HTTPServlet"));
    }

    @Test
    public void testCamelCaseWithNumbersToUnderScore() {
        Assert.assertEquals("simple_size_2", NameUtils.camelCaseToUnderscore("simpleSize2"));
    }

    @Test
    public void testCamelCaseWithNumbersToUnderScoreTypemapper() {

        // different behavior in typemapper. This test exists just to notify the differences between both algorithms
        Assert.assertEquals("simple_size2", PgTypeHelper.camelCaseToUnderScore("simpleSize2"));
    }

}
