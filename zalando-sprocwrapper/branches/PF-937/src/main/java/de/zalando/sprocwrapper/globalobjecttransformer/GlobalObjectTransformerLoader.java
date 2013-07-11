package de.zalando.sprocwrapper.globalobjecttransformer;

import java.util.Set;

import org.reflections.Reflections;

import org.reflections.scanners.TypeAnnotationsScanner;

import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;

import de.zalando.sprocwrapper.globalobjecttransformer.annotation.GlobalObjectMapper;
import de.zalando.sprocwrapper.globalvaluetransformer.ValueTransformerUtils;

import de.zalando.typemapper.core.fieldMapper.GlobalObjectMapperRegistry;
import de.zalando.typemapper.core.fieldMapper.ObjectMapper;

public class GlobalObjectTransformerLoader {

    private static final String GLOBAL_OBJECT_TRANSFORMER_SEARCH_NAMESPACE =
        "global.object.transformer.search.namespace";

    // you need to set the namespace to a valid value like: org.doodlejump
    private static String namespaceToScan = "de.zalando";

    private static final Logger LOG = LoggerFactory.getLogger(GlobalObjectTransformerLoader.class);
    private static boolean scannedMapper = false;

    public static synchronized ObjectMapper getObjectMapperForClass(final Class<?> genericType)
        throws InstantiationException, IllegalAccessException {

        // did we already scanned the classpath for global value transformers?
        if (scannedMapper == false) {
            final Predicate<String> filter = new Predicate<String>() {
                @Override
                public boolean apply(final String input) {
                    return GlobalObjectMapper.class.getCanonicalName().equals(input);
                }
            };

            // last to get the namespace from the system environment
            String myNameSpaceToScan = null;
            try {
                myNameSpaceToScan = System.getenv(GLOBAL_OBJECT_TRANSFORMER_SEARCH_NAMESPACE);
            } catch (final Exception e) {
                // ignore - e.g. if a security manager exists and permissions are denied.
            }

            if (Strings.isNullOrEmpty(myNameSpaceToScan)) {

                // last to use the given namespace
                myNameSpaceToScan = namespaceToScan;
            }

            if (!Strings.isNullOrEmpty(myNameSpaceToScan)) {

                final Reflections reflections = new Reflections(new ConfigurationBuilder().filterInputsBy(
                            new FilterBuilder.Include(FilterBuilder.prefix(myNameSpaceToScan))).setUrls(
                            ClasspathHelper.forPackage(myNameSpaceToScan)).setScanners(new TypeAnnotationsScanner()
                                .filterResultsBy(filter)));
                final Set<Class<?>> typesAnnotatedWith2 = reflections.getTypesAnnotatedWith(GlobalObjectMapper.class);

                for (final Class<?> foundGlobalObjectTransformer : typesAnnotatedWith2) {
                    final Class<?> valueTransformerReturnType = ValueTransformerUtils.getUnmarshalFromDbNodeClass(
                            foundGlobalObjectTransformer);
                    if (valueTransformerReturnType != null) {

                        GlobalObjectMapperRegistry.register(valueTransformerReturnType,
                            (ObjectMapper) foundGlobalObjectTransformer.newInstance());
                        LOG.debug("Global Object Mapper [{}] for type [{}] registered. ",
                            foundGlobalObjectTransformer.getSimpleName(), valueTransformerReturnType.getSimpleName());
                    } else {
                        LOG.error(
                            "Could add global Object Mapper [{}] to global registry. Could not find method unmarshalFromDb.",
                            foundGlobalObjectTransformer);
                    }
                }

            }

            scannedMapper = true;
        }

        return GlobalObjectMapperRegistry.getValueTransformerForClass(genericType);
    }

    /**
     * Use this static function to set the namespace to scan.
     *
     * @param  newNamespace  the new namespace to be searched for
     *                       {@link de.zalando.sprocwrapper.globalvaluetransformer.annotation.GlobalValueTransformer}
     */
    public static void changeNamespaceToScan(final String newNamespace) {
        namespaceToScan = newNamespace;
        scannedMapper = false;
    }
}
