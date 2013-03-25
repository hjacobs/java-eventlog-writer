package de.zalando.jpa.eclipselink;

import static org.mockito.Matchers.any;

import static org.mockito.Mockito.only;
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

    @Before
    public void setUp() {

        customizer = new DirectToFieldMappingEnumTypeConverterCustomizer();
        Assert.assertEquals(DirectToFieldMapping.class, customizer.supportedDatabaseMapping());

        mapping = Mockito.mock(DirectToFieldMapping.class);
        dataBaseField = Mockito.mock(DatabaseField.class);
        Mockito.when(mapping.getField()).thenReturn(dataBaseField);
    }

    @Test
    public void testConverterCustomizerOnNonEnum() {

        // when
        Mockito.when(mapping.getAttributeClassification()).thenReturn(String.class);

        customizer.customizeConverter(mapping, MockSessionCreator.create());

        // verify no further interaction with mapping if there is no enum
        Mockito.verify(mapping, only()).getAttributeClassification();
    }

    @Test
    public void testConverterCustomizerOnEnum() {

        // when
        Mockito.when(mapping.getAttributeClassification()).thenReturn(Enum.class);

        customizer.customizeConverter(mapping, MockSessionCreator.create());

        // verify converter will be set for the mapping
        Mockito.verify(mapping, times(1)).setConverter(any(EnumTypeConverter.class));
    }

}
