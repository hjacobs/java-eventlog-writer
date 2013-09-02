package de.zalando.jpa.eclipselink.customizer.databasemapping;

import org.eclipse.persistence.mappings.DatabaseMapping;

import org.junit.Test;

import org.mockito.Mockito;

import de.zalando.jpa.eclipselink.MockSessionCreator;

/**
 * @author  jbellmann
 */
public class NoOpColumnNameCustomizerTest {

    NoOpColumnNameCustomizer customizer = new NoOpColumnNameCustomizer();

    @Test
    public void testNoOpOnInvocation() {

        DatabaseMapping mapping = Mockito.mock(DatabaseMapping.class);
        customizer.customizeColumnName("purchase_order_head", mapping, MockSessionCreator.create());
        Mockito.verifyZeroInteractions(mapping);
    }

}
