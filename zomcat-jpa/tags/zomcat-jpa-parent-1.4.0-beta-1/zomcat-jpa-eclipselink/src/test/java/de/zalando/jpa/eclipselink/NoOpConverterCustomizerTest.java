package de.zalando.jpa.eclipselink;

import static org.mockito.Mockito.only;

import org.eclipse.persistence.mappings.DatabaseMapping;

import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author  jbellmann
 */
public class NoOpConverterCustomizerTest {

    @Test
    public void testNoOperation() {
        NoOpConverterCustomizer noOpCustomizer = new NoOpConverterCustomizer();
        DatabaseMapping mapping = Mockito.mock(DatabaseMapping.class);
        noOpCustomizer.customizeConverter(mapping, MockSessionCreator.create());

        // we only use getAttributeName in the logging statement, make sure this is only call to mapping
        Mockito.verify(mapping, only()).getAttributeName();
    }

}
