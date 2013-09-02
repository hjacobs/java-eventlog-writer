package de.zalando.jpa.eclipselink.customizer.databasemapping;

import static org.mockito.Mockito.only;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.mappings.DirectToFieldMapping;

import org.junit.Ignore;
import org.junit.Test;

import org.mockito.Mockito;

import de.zalando.jpa.eclipselink.MockSessionCreator;

/**
 * @author  jbellmann
 */
@Ignore
public class NoOpConverterCustomizerTest {

    @Test
    public void testNoOperation() {
        NoOpConverterCustomizer noOpCustomizer = new NoOpConverterCustomizer();
        DirectToFieldMapping mapping = Mockito.spy(new DirectToFieldMapping());
        mapping.setAttributeName("brandCode");
        mapping.setField(new DatabaseField());
        mapping.setAttributeClassification(String.class);
// Mockito.spy(mapping);

        noOpCustomizer.customizeConverter(mapping, MockSessionCreator.create());

        // we only use getAttributeName in the logging statement, make sure this is only call to mapping
        Mockito.verify(mapping.getAttributeName(), only());
    }

}
