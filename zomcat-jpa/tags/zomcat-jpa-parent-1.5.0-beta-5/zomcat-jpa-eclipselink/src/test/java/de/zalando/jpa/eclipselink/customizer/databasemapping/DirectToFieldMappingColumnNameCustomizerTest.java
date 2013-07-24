package de.zalando.jpa.eclipselink.customizer.databasemapping;

import static org.mockito.Matchers.eq;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.mappings.DirectToFieldMapping;

import org.junit.Test;

import org.mockito.Mockito;

import de.zalando.jpa.eclipselink.AttributeHolderBean;
import de.zalando.jpa.eclipselink.MockSessionCreator;

/**
 * @author  jbellmann
 */
public class DirectToFieldMappingColumnNameCustomizerTest {

    DirectToFieldMappingColumnNameCustomizer customizer = new DirectToFieldMappingColumnNameCustomizer();

    @Test
    public void testCustomization() {
        DirectToFieldMapping mapping = Mockito.mock(DirectToFieldMapping.class);
        DatabaseField dataBaseField = Mockito.mock(DatabaseField.class);
        ClassDescriptor classDescriptor = new ClassDescriptor();
        classDescriptor.setJavaClass(AttributeHolderBean.class);
        Mockito.when(mapping.getField()).thenReturn(dataBaseField);
        Mockito.when(mapping.getAttributeName()).thenReturn("orderStatus");
        Mockito.when(mapping.getDescriptor()).thenReturn(classDescriptor);

        // invoke
        customizer.customizeColumnName("purchase_order_head", mapping, MockSessionCreator.create());

        //
        Mockito.verify(dataBaseField).setName(eq("poh_order_status"));
    }

    @Test
    public void booleanCustomizationStartsWithIsPrefix() {
        DirectToFieldMapping mapping = Mockito.mock(DirectToFieldMapping.class);
        DatabaseField dataBaseField = Mockito.mock(DatabaseField.class);
        ClassDescriptor classDescriptor = new ClassDescriptor();
        classDescriptor.setJavaClass(AttributeHolderBean.class);
        Mockito.when(mapping.getField()).thenReturn(dataBaseField);
        Mockito.when(mapping.getAttributeName()).thenReturn("ordered");
        Mockito.when(mapping.getAttributeClassification()).thenReturn(Boolean.class);
        Mockito.when(mapping.getDescriptor()).thenReturn(classDescriptor);

        // invoke
        customizer.customizeColumnName("purchase_order_head", mapping, MockSessionCreator.create());

        //
        Mockito.verify(dataBaseField).setName(eq("poh_is_ordered"));
    }
}
