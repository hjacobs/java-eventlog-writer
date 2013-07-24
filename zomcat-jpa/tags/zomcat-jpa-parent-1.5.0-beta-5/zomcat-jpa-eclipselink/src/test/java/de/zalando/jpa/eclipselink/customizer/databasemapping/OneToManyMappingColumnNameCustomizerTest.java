package de.zalando.jpa.eclipselink.customizer.databasemapping;

import org.eclipse.persistence.mappings.OneToManyMapping;

import org.junit.Test;

import org.mockito.Mockito;

import de.zalando.jpa.eclipselink.MockSessionCreator;

/**
 * @author  jbellmann
 */
public class OneToManyMappingColumnNameCustomizerTest {

    OneToManyMappingColumnNameCustomizer customizer = new OneToManyMappingColumnNameCustomizer();

    @Test
    public void testWithoutAnyFurtherActionOnMapping() {
        OneToManyMapping mapping = Mockito.mock(OneToManyMapping.class);
        customizer.customizeColumnName("purchase_order_head", mapping, MockSessionCreator.create());
        Mockito.verifyZeroInteractions(mapping);
    }

}
