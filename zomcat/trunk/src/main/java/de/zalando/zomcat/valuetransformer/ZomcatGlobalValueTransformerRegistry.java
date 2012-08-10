package de.zalando.zomcat.valuetransformer;

import java.lang.reflect.Method;

import java.util.Set;

import org.reflections.Reflections;

import org.reflections.scanners.TypeAnnotationsScanner;

import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;

import com.typemapper.core.ValueTransformer;
import com.typemapper.core.fieldMapper.GlobalValueTransformerRegistry;

import de.zalando.zomcat.util.ReflectionUtils;
import de.zalando.zomcat.valuetransformer.annotation.GlobalValueTransformer;

public class ZomcatGlobalValueTransformerRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(ZomcatGlobalValueTransformerRegistry.class);
    private static boolean scannedClasspath = false;

    public static synchronized ValueTransformer<?, ?> getValueTransformerForClass(final Class<?> genericType)
        throws InstantiationException, IllegalAccessException {

        // did we already scanned the classpath for global value transformers? (even before @autowire?)
        // we need a delegation here, because the registry is in an GITHUB project
        // that makes things COMPLICATED.
        if (scannedClasspath == false) {
            final Predicate<String> filter = new Predicate<String>() {
                @Override
                public boolean apply(final String input) {
                    return GlobalValueTransformer.class.getCanonicalName().equals(input);
                }
            };

            final Reflections reflections = new Reflections(new ConfigurationBuilder().filterInputsBy(
                        new FilterBuilder.Include(FilterBuilder.prefix("de.zalando"))).setUrls(
                        ClasspathHelper.forPackage("de.zalando")).setScanners(new TypeAnnotationsScanner()
                            .filterResultsBy(filter)));
            final Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(GlobalValueTransformer.class);
            for (final Class<?> foundGlobalValueTransformer : typesAnnotatedWith) {
                final Method method = ReflectionUtils.findMethod(foundGlobalValueTransformer, "unmarshalFromDb");
                if (method != null) {
                    final Class<?> valueTransformerReturnType = method.getReturnType();
                    GlobalValueTransformerRegistry.register(valueTransformerReturnType,
                        (ValueTransformer<?, ?>) foundGlobalValueTransformer.newInstance());
                    LOG.debug("Global Value Transformer [{}] for type [{}] registered. ",
                        foundGlobalValueTransformer.getSimpleName(), valueTransformerReturnType.getSimpleName());
                } else {
                    LOG.error(
                        "Could add global transformer [{}] to global registry. Could not find method unmarshalFromDb.",
                        foundGlobalValueTransformer);
                }
            }

            scannedClasspath = true;
        }

        return GlobalValueTransformerRegistry.getValueTransformerForClass(genericType);
    }

}
