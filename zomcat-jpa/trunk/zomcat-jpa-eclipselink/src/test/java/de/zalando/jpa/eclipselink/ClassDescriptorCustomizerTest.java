package de.zalando.jpa.eclipselink;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.ManyToOneMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.oxm.mappings.XMLInverseReferenceMapping;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author  jbellmann
 */
public class ClassDescriptorCustomizerTest {

    @Test
    public void testNoOpColumnNameCustomizer() {
        DefaultClassDescriptorCustomizer clazzDescriptorCustomizer = new DefaultClassDescriptorCustomizer();

        ColumnNameCustomizer<DatabaseMapping> result = clazzDescriptorCustomizer.getColumnNameCustomizer(
                new XMLInverseReferenceMapping());

        assertCustomizer(result, NoOpColumnNameCustomizer.class);
    }

    @Test
    public void testColumnNameCustomizerSelection() {

        DefaultClassDescriptorCustomizer clazzDescriptorCustomizer = new DefaultClassDescriptorCustomizer();

        // columnNames
        clazzDescriptorCustomizer.registerColumnNameCustomizer(new DirectToFieldMappingColumnNameCustomizer());
        clazzDescriptorCustomizer.registerColumnNameCustomizer(new ManyToOneMappingColumnNameCustomizer());
        clazzDescriptorCustomizer.registerColumnNameCustomizer(new OneToManyMappingColumnNameCustomizer());

        // enumConverter
        clazzDescriptorCustomizer.registerConverterCustomizer(new DirectToFieldMappingEnumTypeConverterCustomizer());

        // invoke
        ColumnNameCustomizer<DatabaseMapping> result = clazzDescriptorCustomizer.getColumnNameCustomizer(
                new DirectToFieldMapping());

        // assert
        assertCustomizer(result, DirectToFieldMappingColumnNameCustomizer.class);

        // invoke
        result = clazzDescriptorCustomizer.getColumnNameCustomizer(new ManyToOneMapping());

        // assert
        assertCustomizer(result, ManyToOneMappingColumnNameCustomizer.class);

        // invoke
        result = clazzDescriptorCustomizer.getColumnNameCustomizer(new OneToManyMapping());

        // assert
        assertCustomizer(result, OneToManyMappingColumnNameCustomizer.class);

        ConverterCustomizer<DatabaseMapping> converterCustomizerResult =
            clazzDescriptorCustomizer.getConverterCustomizer(new DirectToFieldMapping());

        assertConverter(converterCustomizerResult, DirectToFieldMappingEnumTypeConverterCustomizer.class);

    }

    private void assertConverter(final ConverterCustomizer<DatabaseMapping> converterCustomizerResult,
            final Class<?> clazz) {
        Assert.assertNotNull(converterCustomizerResult);
        Assert.assertEquals(clazz, converterCustomizerResult.getClass());

    }

    protected void assertCustomizer(final ColumnNameCustomizer<DatabaseMapping> customizer,
            final Class<?> expectedClass) {
        Assert.assertNotNull("Customizer should not be null", customizer);
        Assert.assertEquals("Class of ColumnNameCustomizer does not match expected CustomizerClass", expectedClass,
            customizer.getClass());
    }

}
