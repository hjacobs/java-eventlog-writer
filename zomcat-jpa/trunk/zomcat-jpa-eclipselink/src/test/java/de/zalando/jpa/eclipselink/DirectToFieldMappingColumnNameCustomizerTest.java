package de.zalando.jpa.eclipselink;

import static org.mockito.Matchers.eq;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.mappings.DirectToFieldMapping;

import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author  jbellmann
 */
public class DirectToFieldMappingColumnNameCustomizerTest {

    DirectToFieldMappingColumnNameCustomizer customizer = new DirectToFieldMappingColumnNameCustomizer();

    @Test
    public void testCustomization() {
        DirectToFieldMapping mapping = Mockito.mock(DirectToFieldMapping.class);
        DatabaseField dataBaseField = Mockito.mock(DatabaseField.class);
        Mockito.when(mapping.getField()).thenReturn(dataBaseField);
        Mockito.when(mapping.getAttributeName()).thenReturn("orderStatus");

        // invoke
        customizer.customizeColumnName("purchase_order_head", mapping, MockSessionCreator.create());

        //
        Mockito.verify(dataBaseField).setName(eq("poh_order_status"));
    }

    @Test
    public void booleanCustomizationStartsWithIsPrefix() {
        DirectToFieldMapping mapping = Mockito.mock(DirectToFieldMapping.class);
        DatabaseField dataBaseField = Mockito.mock(DatabaseField.class);
        Mockito.when(mapping.getField()).thenReturn(dataBaseField);
        Mockito.when(mapping.getAttributeName()).thenReturn("ordered");
        Mockito.when(mapping.getAttributeClassification()).thenReturn(Boolean.class);

        // invoke
        customizer.customizeColumnName("purchase_order_head", mapping, MockSessionCreator.create());

        //
        Mockito.verify(dataBaseField).setName(eq("poh_is_ordered"));
    }
}
