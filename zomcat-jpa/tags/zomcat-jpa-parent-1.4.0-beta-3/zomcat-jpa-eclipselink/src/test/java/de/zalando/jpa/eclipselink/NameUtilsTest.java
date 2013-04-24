package de.zalando.jpa.eclipselink;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link NameUtils}.
 *
 * @author  jbellmann
 */
public class NameUtilsTest {

    @Test(expected = IllegalArgumentException.class)
    public void testIconizeTableNameWithNullArgument() {
        NameUtils.iconizeTableName(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIconizedTableNameWithEmptyArgument() {
        NameUtils.iconizeTableName("");
    }

    @Test
    public void testIconizeTableName() {
        Assert.assertEquals("tb", NameUtils.iconizeTableName("TA_BLENAME"));
        Assert.assertEquals("tb", NameUtils.iconizeTableName("thisIs_BlOED"));
        Assert.assertEquals("km", NameUtils.iconizeTableName("KLAUS_MEIER"));
        Assert.assertEquals("poh", NameUtils.iconizeTableName("purchase_order_head"));
    }

    @Test
    public void testBuildFieldName() {
        Assert.assertEquals("poh_brand_code", NameUtils.buildFieldName("purchase_order_head", "brandCode"));
        Assert.assertEquals("poh_purchaser_email", NameUtils.buildFieldName("purchase_order_head", "purchaserEmail"));
        Assert.assertEquals("poh_supplier_document_number",
            NameUtils.buildFieldName("purchase_order_head", "supplierDocumentNumber"));
        Assert.assertEquals("poh_order_date", NameUtils.buildFieldName("purchase_order_head", "orderDate"));
    }
}
