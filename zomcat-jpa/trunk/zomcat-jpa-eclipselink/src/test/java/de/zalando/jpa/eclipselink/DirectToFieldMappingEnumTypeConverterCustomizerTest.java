package de.zalando.jpa.eclipselink;

import static org.mockito.Matchers.any;

import static org.mockito.Mockito.times;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.converters.Converter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author  jbellmann
 */
public class DirectToFieldMappingEnumTypeConverterCustomizerTest {

    DirectToFieldMappingEnumTypeConverterCustomizer customizer;

    DirectToFieldMapping mapping;
    DatabaseField dataBaseField;

    Converter eclipseConverter;

    @Before
    public void setUp() {

        customizer = new DirectToFieldMappingEnumTypeConverterCustomizer();
        Assert.assertEquals(DirectToFieldMapping.class, customizer.supportedDatabaseMapping());

        mapping = Mockito.mock(DirectToFieldMapping.class);
        dataBaseField = Mockito.mock(DatabaseField.class);

        eclipseConverter = Mockito.mock(Converter.class);

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
    @Ignore
    public void testConverterCustomizerOnEnum() {

        // when
// Mockito.when(mapping.getAttributeClassification()).thenReturn(Enum.class);
        org.eclipse.persistence.mappings.converters.EnumTypeConverter enumTypeConverter = Mockito.mock(
                org.eclipse.persistence.mappings.converters.EnumTypeConverter.class);
        /*Mockito.when(eclipseConverter.getClass()).thenReturn(
         *  org.eclipse.persistence.mappings.converters.EnumTypeConverter.class);*/
// Mockito.when(eclipseConverter.getEnumClass()).thenReturn(Status.class);

        customizer.customizeConverter(mapping, MockSessionCreator.create());

        // verify converter will be set for the mapping
        Mockito.verify(mapping, times(1)).setConverter(any(EnumTypeConverter.class));
    }

}
