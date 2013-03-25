package de.zalando.jpa.eclipselink;

import org.eclipse.persistence.mappings.DatabaseMapping;

import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author  jbellmann
 */
public class NoOpColumnNameCustomizerTest {

    NoOpColumnNameCustomizer customizer = new NoOpColumnNameCustomizer();

    @Test
    public void testNoOpOnInvocation() {

        DatabaseMapping mapping = Mockito.mock(DatabaseMapping.class);
        customizer.customizeColumnName("purchase_order_head", mapping);
        Mockito.verifyZeroInteractions(mapping);
    }

}
