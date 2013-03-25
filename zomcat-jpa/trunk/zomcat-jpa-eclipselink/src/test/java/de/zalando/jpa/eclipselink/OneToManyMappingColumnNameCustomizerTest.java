package de.zalando.jpa.eclipselink;

import org.eclipse.persistence.mappings.OneToManyMapping;

import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author  jbellmann
 */
public class OneToManyMappingColumnNameCustomizerTest {

    OneToManyMappingColumnNameCustomizer customizer = new OneToManyMappingColumnNameCustomizer();

    @Test
    public void testWithoutAnyFurtherActionOnMapping() {
        OneToManyMapping mapping = Mockito.mock(OneToManyMapping.class);
        customizer.customizeColumnName("purchase_order_head", mapping);
        Mockito.verifyZeroInteractions(mapping);
    }

}
