package de.zalando.jpa.eclipselink.customizer.session;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.sessions.Session;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import de.zalando.jpa.eclipselink.LogSupport;
import de.zalando.jpa.eclipselink.customizer.classdescriptor.ClassDescriptorCustomizer;
import de.zalando.jpa.eclipselink.customizer.classdescriptor.DefaultClassDescriptorCustomizer;
import de.zalando.jpa.eclipselink.customizer.databasemapping.ColumnNameCustomizer;
import de.zalando.jpa.eclipselink.customizer.databasemapping.ConverterCustomizer;
import de.zalando.jpa.eclipselink.customizer.databasemapping.DirectToFieldMappingColumnNameCustomizer;
import de.zalando.jpa.eclipselink.customizer.databasemapping.ManyToManyMappingCustomizer;
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
     * Returns a list of all {@link ColumnNameCustomizer}s for default behavior.
     *
     * @return  list of {@link ColumnNameCustomizer}
     *
     * @see     DirectToFieldMappingColumnNameCustomizer
     * @see     OneToManyMappingColumnNameCustomizer
     * @see     OneToOneMappingColumnNameCustomizer
     * @see     ManyToManyMappingCustomizer
     */
    public static List<ColumnNameCustomizer> defaultColumnNameCustomizers() {
        List<ColumnNameCustomizer> columnNameCustomizer = Lists.newLinkedList();

        columnNameCustomizer.add(new DirectToFieldMappingColumnNameCustomizer());

        columnNameCustomizer.add(new OneToManyMappingColumnNameCustomizer());
        columnNameCustomizer.add(new OneToOneMappingColumnNameCustomizer());

        columnNameCustomizer.add(new ManyToOneMappingColumnNameCustomizer());
        columnNameCustomizer.add(new ManyToManyMappingCustomizer());
        return columnNameCustomizer;
    }

    /**
     * Returns an empty Lists until now.
     *
     * @return
     */
    public static List<ConverterCustomizer> defaultConverterCustomizer() {
        return Lists.newArrayList();
    }

    /**
     * Returns an {@link ClassDescriptorCustomizer} that wrapped the default {@link ColumnNameCustomizer}s.
     *
     * @see  DefaultClassDescriptorCustomizer
     */
    public static ClassDescriptorCustomizer defaultColumnNameClassDescriptorCustomizer() {
        return newClassDescriptorCustomizer(defaultColumnNameCustomizers(), new ArrayList<ConverterCustomizer>());
    }

    /**
     * Returns an {@link ClassDescriptorCustomizer} that wrapped all {@link ColumnNameCustomizer}s and
     * {@link ConverterCustomizer}s provided by the arguments.
     *
     * @param   columnNameCustomizers
     * @param   converterCustomizers
     *
     * @return
     */
    public static ClassDescriptorCustomizer newClassDescriptorCustomizer(
            final List<ColumnNameCustomizer> columnNameCustomizers,
            final List<ConverterCustomizer> converterCustomizers) {
        DefaultClassDescriptorCustomizer d = new DefaultClassDescriptorCustomizer();
        d.registerColumnNameCustomizer(columnNameCustomizers);
        d.registerConverterCustomizer(converterCustomizers);
        return d;
    }

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
     * @return  and builder to compose multiple {@link ClassDescriptorCustomizer} into one
     *          {@link ClassDescriptorCustomizer}.
     */
    public static ClassDescriptorCustomizerBuilder builder() {
        return new Builder();
    }

    /**
     * {@link ClassDescriptorCustomizerBuilder} implementation.
     *
     * @author  jbellmann
     */
    static final class Builder implements ClassDescriptorCustomizerBuilder {

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
