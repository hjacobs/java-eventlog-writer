package de.zalando.jpa.eclipselink;

import static org.mockito.Matchers.any;

import static org.mockito.Mockito.times;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.mappings.DirectToFieldMapping;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author  jbellmann
 */
public class DirectToFieldMappingEnumTypeConverterCustomizerTest {

    DirectToFieldMappingEnumTypeConverterCustomizer customizer;

    DirectToFieldMapping mapping;
    DatabaseField dataBaseField;

    org.eclipse.persistence.mappings.converters.EnumTypeConverter eclipseConverter;

    @Before
    public void setUp() {

        customizer = new DirectToFieldMappingEnumTypeConverterCustomizer();
        Assert.assertEquals(DirectToFieldMapping.class, customizer.supportedDatabaseMapping());

        mapping = Mockito.mock(DirectToFieldMapping.class);
        dataBaseField = Mockito.mock(DatabaseField.class);

        eclipseConverter = Mockito.mock(org.eclipse.persistence.mappings.converters.EnumTypeConverter.class);

        Mockito.when(mapping.getConverter()).thenReturn(eclipseConverter);
    }

    @Test
    public void testConverterCustomizerOnNonEnum() {

        // when
        Mockito.when(mapping.getConverter()).thenReturn(null);

        customizer.customizeConverter(mapping, MockSessionCreator.create());

        // verify no further interaction with mapping if there is no enum
        Mockito.verify(mapping, Mockito.never()).setConverter(any(EnumTypeConverter.class));
    }

    @Test
    public void testConverterCustomizerOnEnum() {

        customizer = new TestDirectToFieldMappingEnumTypeCustomizer();

        Mockito.when(eclipseConverter.getEnumClass()).thenReturn(Status.class);
        Mockito.when(mapping.getField()).thenReturn(dataBaseField);
        Mockito.when(dataBaseField.getName()).thenReturn("po_order_status");
        customizer.customizeConverter(mapping, MockSessionCreator.create());

        // verify converter will be set for the mapping
        Mockito.verify(mapping, times(1)).setConverter(any(EnumTypeConverter.class));
    }

    /**
     * Just for Testing.
     *
     * @author  jbellmann
     */
    static class TestDirectToFieldMappingEnumTypeCustomizer extends DirectToFieldMappingEnumTypeConverterCustomizer {

        @Override
        protected boolean hasEnumTypeConverter(final DirectToFieldMapping databaseMapping) {
            return true;
        }

    }

}
