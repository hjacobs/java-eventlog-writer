package de.zalando.jpa.eclipselink.customizer.session;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.sessions.Session;

import com.google.common.base.Preconditions;

import de.zalando.jpa.eclipselink.LogSupport;
import de.zalando.jpa.eclipselink.customizer.classdescriptor.ClassDescriptorCustomizer;
import de.zalando.jpa.eclipselink.customizer.classdescriptor.DefaultClassDescriptorCustomizer;
import de.zalando.jpa.eclipselink.customizer.databasemapping.ColumnNameCustomizer;
import de.zalando.jpa.eclipselink.customizer.databasemapping.ConverterCustomizer;
import de.zalando.jpa.eclipselink.customizer.databasemapping.DirectToFieldMappingColumnNameCustomizer;
import de.zalando.jpa.eclipselink.customizer.databasemapping.ManyToOneMappingColumnNameCustomizer;
import de.zalando.jpa.eclipselink.customizer.databasemapping.OneToManyMappingColumnNameCustomizer;
import de.zalando.jpa.eclipselink.customizer.databasemapping.OneToOneMappingColumnNameCustomizer;

/**
 * Base for {@link SessionCustomizer} implementations.
 *
 * @author  jbellmann
 */
public abstract class AbstractZomcatSessionCustomizer extends LogSupport implements SessionCustomizer {

    @SuppressWarnings("rawtypes")
    @Override
    public void customize(final Session session) throws Exception {
        logInfo(session, CUS_SESSION_START);

        logSessionProperties(session);

        // process customization on all clazzDescriptors
        Map<Class, ClassDescriptor> clazzDescriptors = session.getDescriptors();
        for (Map.Entry<Class, ClassDescriptor> descriptorEntry : clazzDescriptors.entrySet()) {

            getClassDescriptorCustomizer().customize(descriptorEntry.getValue(), session);
        }

        logInfo(session, CUS_SESSION_END);
    }

    protected void logSessionProperties(final Session session) {
        logFine(session, SESSION_PROPS);
        for (Map.Entry<Object, Object> entry : session.getProperties().entrySet()) {
            logFine(session, KEY_VALUE, entry.getKey().toString(), entry.getValue().toString());
        }
    }

    public abstract ClassDescriptorCustomizer getClassDescriptorCustomizer();

    /**
     * An BuilderStep.
     *
     * @author  jbellmann
     */
    public static interface ClassDescriptorCustomizerBuilder {

        ClassDescriptorCustomizerBuilder with(ClassDescriptorCustomizer clazzDescriptorCustomizer);

        ClassDescriptorCustomizer build();
    }

    /**
     * An BuilderStep.
     *
     * @author  jbellmann
     */
    public static interface ColumnNameCustomizerCompositeBuilder {

        ColumnNameCustomizerCompositeBuilder with(ColumnNameCustomizer columnNameCustomizer);

        ColumnNameCustomizerCompositeBuilder with(ConverterCustomizer converterCustomizer);

        ClassDescriptorCustomizerBuilder and();

        ClassDescriptorCustomizer build();

    }

    public static ColumnNameCustomizerCompositeBuilder newBuilder() {
        return new CNCustomizerBuilder();
    }

    public static ColumnNameCustomizerCompositeBuilder newBuilderWithDefaults() {
        return new CNCustomizerBuilder().with(new DirectToFieldMappingColumnNameCustomizer())
                                        .with(new OneToManyMappingColumnNameCustomizer())
                                        .with(new OneToOneMappingColumnNameCustomizer()).with(
                                            new ManyToOneMappingColumnNameCustomizer());
    }

    /**
     * @author  jbellmann
     */
    public static final class CNCustomizerBuilder implements ColumnNameCustomizerCompositeBuilder {

        private final List<ColumnNameCustomizer> columnNameCustomizer = new ArrayList<ColumnNameCustomizer>();
        private final List<ConverterCustomizer> converterCustomizer = new ArrayList<ConverterCustomizer>();

        @Override
        public ColumnNameCustomizerCompositeBuilder with(final ColumnNameCustomizer columnNameCustomizer) {
            this.columnNameCustomizer.add(columnNameCustomizer);
            return this;
        }

        @Override
        public ColumnNameCustomizerCompositeBuilder with(final ConverterCustomizer converterCustomizer) {
            this.converterCustomizer.add(converterCustomizer);
            return this;
        }

        @Override
        public ClassDescriptorCustomizerBuilder and() {

            return new Builder().with(build());
        }

        @Override
        public ClassDescriptorCustomizer build() {
            DefaultClassDescriptorCustomizer dcdc = new DefaultClassDescriptorCustomizer();
            for (ColumnNameCustomizer<?> cnc : this.columnNameCustomizer) {
                dcdc.registerColumnNameCustomizer(cnc);
            }

            //
            for (ConverterCustomizer<?> cnc : this.converterCustomizer) {
                dcdc.registerConverterCustomizer(cnc);
            }

            return dcdc;
        }
    }

    public static ClassDescriptorCustomizerBuilder newComposite() {
        return new Builder();
    }

    /**
     * {@link ClassDescriptorCustomizerBuilder} implementation.
     *
     * @author  jbellmann
     */
    public static final class Builder implements ClassDescriptorCustomizerBuilder {

        private final List<ClassDescriptorCustomizer> customizers = new ArrayList<ClassDescriptorCustomizer>();

        public Builder with(final ClassDescriptorCustomizer classDescriptorCustomizer) {
            Preconditions.checkNotNull(classDescriptorCustomizer, "ClassDescriptorCustomizer should never be null");
            this.customizers.add(classDescriptorCustomizer);
            return this;
        }

        public ClassDescriptorCustomizer build() {

            return CompositeClassDescriptorCustomizer.build(this.customizers.toArray(
                        new ClassDescriptorCustomizer[this.customizers.size()]));

        }
    }
}
