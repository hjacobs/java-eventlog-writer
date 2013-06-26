package de.zalando.jpa.eclipselink.customizer.databasemapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import java.util.Map;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.ManyToOneMapping;
import org.eclipse.persistence.sessions.Session;

import org.springframework.util.ReflectionUtils;

import com.google.common.collect.Maps;

import de.zalando.jpa.eclipselink.LogSupport;
import de.zalando.jpa.eclipselink.customizer.databasemapping.support.ColumnFieldInspector;
import de.zalando.jpa.eclipselink.customizer.databasemapping.support.DatabaseMappingLogger;
import de.zalando.jpa.eclipselink.customizer.databasemapping.support.DirectToFieldMappingLogger;
import de.zalando.jpa.eclipselink.customizer.databasemapping.support.EntityFieldInspector;
import de.zalando.jpa.eclipselink.customizer.databasemapping.support.ManyToOneMappingLogger;

/**
 * @param   <T>
 *
 * @author  jbellmann
 */
public abstract class AbstractColumnNameCustomizer<T extends DatabaseMapping> extends LogSupport
    implements ColumnNameCustomizer<T> {

    private Class<T> supportedMappingType;

    private Map<Class<? extends DatabaseMapping>, DatabaseMappingLogger<?>> loggerMap = Maps.newHashMap();

    protected AbstractColumnNameCustomizer(final Class<T> supportedMappingType) {
        this.supportedMappingType = supportedMappingType;
        loggerMap.put(DirectToFieldMapping.class, new DirectToFieldMappingLogger());
        loggerMap.put(ManyToOneMapping.class, new ManyToOneMappingLogger());
    }

    @Override
    public Class<T> supportedDatabaseMapping() {
        return this.supportedMappingType;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected void logDatabaseMapping(final DatabaseMapping databaseMapping, final Session session) {
        DatabaseMappingLogger logger = loggerMap.get(databaseMapping.getClass());
        if (logger != null) {
            logger.logDatabaseMapping(databaseMapping, session);
        }
    }

    protected EntityFieldInspector<? extends Annotation> getFieldInspector(final DatabaseMapping databaseMapping) {
        final String attributeName = databaseMapping.getAttributeName();
        final Class<?> entityClass = databaseMapping.getDescriptor().getJavaClass();

        final Field field = ReflectionUtils.findField(entityClass, attributeName);
        return new ColumnFieldInspector(field);
    }

}
