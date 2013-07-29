package de.zalando.jpa.eclipselink.customizer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests for {@link de.zalando.jpa.eclipselink.customizer.NameUtils}.
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
        assertEquals("tb", NameUtils.iconizeTableName("TA_BLENAME"));
        assertEquals("tb", NameUtils.iconizeTableName("thisIs_BlOED"));
        assertEquals("km", NameUtils.iconizeTableName("KLAUS_MEIER"));
        assertEquals("poh", NameUtils.iconizeTableName("purchase_order_head"));
    }

    @Test
    public void testBuildFieldName() {
        assertEquals("poh_brand_code", NameUtils.buildFieldName("purchase_order_head", "brandCode"));
        assertEquals("poh_purchaser_email", NameUtils.buildFieldName("purchase_order_head", "purchaserEmail"));
        assertEquals("poh_supplier_document_number",
            NameUtils.buildFieldName("purchase_order_head", "supplierDocumentNumber"));
        assertEquals("poh_order_date", NameUtils.buildFieldName("purchase_order_head", "orderDate"));
    }

    @Test
    public void buildBooleanFieldNames() {
        assertEquals("poh_is_ordered", NameUtils.buildBooleanFieldName("purchase_order_head", "ordered"));

        // this is ugly style, but seen in current projects
        assertEquals("poh_is_ordered", NameUtils.buildBooleanFieldName("purchase_order_head", "isOrdered"));
        assertEquals("poh_is_ordered", NameUtils.buildBooleanFieldName("purchase_order_head", "isordered"));
    }
}
