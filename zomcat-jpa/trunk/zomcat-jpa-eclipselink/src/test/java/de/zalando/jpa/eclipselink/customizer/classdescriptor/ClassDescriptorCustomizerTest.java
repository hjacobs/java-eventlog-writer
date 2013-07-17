package de.zalando.jpa.eclipselink.customizer.classdescriptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.ManyToOneMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.oxm.mappings.XMLInverseReferenceMapping;

import org.junit.AfterClass;
import org.junit.Test;

import de.zalando.jpa.eclipselink.customizer.databasemapping.ColumnNameCustomizer;
import de.zalando.jpa.eclipselink.customizer.databasemapping.ConverterCustomizer;
import de.zalando.jpa.eclipselink.customizer.databasemapping.CustomizerRegistry;
import de.zalando.jpa.eclipselink.customizer.databasemapping.DirectToFieldMappingColumnNameCustomizer;
import de.zalando.jpa.eclipselink.customizer.databasemapping.DirectToFieldMappingEnumTypeConverterCustomizer;
import de.zalando.jpa.eclipselink.customizer.databasemapping.ManyToOneMappingColumnNameCustomizer;
import de.zalando.jpa.eclipselink.customizer.databasemapping.NoOpColumnNameCustomizer;
import de.zalando.jpa.eclipselink.customizer.databasemapping.OneToManyMappingColumnNameCustomizer;

/**
 * @author  jbellmann
 */
public class ClassDescriptorCustomizerTest {

    @AfterClass
    public static void tearDownOnce() {
        CustomizerRegistry.get().clear();
    }

    @Test
    public void testNoOpColumnNameCustomizer() {
        ColumnNameCustomizer<DatabaseMapping> result = CustomizerRegistry.get().getColumnNameCustomizer(
                new XMLInverseReferenceMapping());

        assertCustomizer(result, NoOpColumnNameCustomizer.class);
    }

    @Test
    public void testColumnNameCustomizerSelection() {

        // columnNames
        CustomizerRegistry.get().registerColumnNameCustomizer(new DirectToFieldMappingColumnNameCustomizer());
        CustomizerRegistry.get().registerColumnNameCustomizer(new ManyToOneMappingColumnNameCustomizer());
        CustomizerRegistry.get().registerColumnNameCustomizer(new OneToManyMappingColumnNameCustomizer());

        // enumConverter
        CustomizerRegistry.get().registerConverterCustomizer(new DirectToFieldMappingEnumTypeConverterCustomizer());

        // invoke
        ColumnNameCustomizer<DatabaseMapping> result = CustomizerRegistry.get().getColumnNameCustomizer(
                new DirectToFieldMapping());

        // assert
        assertCustomizer(result, DirectToFieldMappingColumnNameCustomizer.class);

        // invoke
        result = CustomizerRegistry.get().getColumnNameCustomizer(new ManyToOneMapping());

        // assert
        assertCustomizer(result, ManyToOneMappingColumnNameCustomizer.class);

        // invoke
        result = CustomizerRegistry.get().getColumnNameCustomizer(new OneToManyMapping());

        // assert
        assertCustomizer(result, OneToManyMappingColumnNameCustomizer.class);

        ConverterCustomizer<DatabaseMapping> converterCustomizerResult = CustomizerRegistry.get()
                                                                                           .getConverterCustomizer(
                                                                                               new DirectToFieldMapping());

        assertConverter(converterCustomizerResult, DirectToFieldMappingEnumTypeConverterCustomizer.class);

    }

    private void assertConverter(final ConverterCustomizer<DatabaseMapping> converterCustomizerResult,
            final Class<?> clazz) {
        assertNotNull(converterCustomizerResult);
        assertEquals(clazz, converterCustomizerResult.getClass());

    }

    protected void assertCustomizer(final ColumnNameCustomizer<DatabaseMapping> customizer,
            final Class<?> expectedClass) {
        assertNotNull("Customizer should not be null", customizer);
        assertEquals("Class of ColumnNameCustomizer does not match expected CustomizerClass", expectedClass,
            customizer.getClass());
    }
}
