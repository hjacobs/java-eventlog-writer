package de.zalando.zomcat.valuetransformer;

import java.lang.reflect.Method;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;

import org.springframework.stereotype.Component;

import com.typemapper.core.ValueTransformer;
import com.typemapper.core.fieldMapper.GlobalValueTransformerRegistry;

import de.zalando.zomcat.util.ReflectionUtils;
import de.zalando.zomcat.valuetransformer.annotation.GlobalValueTransformer;

@Component
@Lazy(value = false)
public class GlobalValueTransformerRegistration {
    private static final Logger LOG = LoggerFactory.getLogger(GlobalValueTransformerRegistration.class);

    @Autowired(required = false)
    private ApplicationContext applicationContext;

    /**
     * This gets called after construction from spring context and registers all global value transformers that are
     * annotated with {@link GlobalValueTransformer}.
     */
    @PostConstruct
    void registerGlobalValueTransformer() {
        if (applicationContext != null) {
            final Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(
                    GlobalValueTransformer.class);
            for (final Object transformer : beansWithAnnotation.values()) {
                if (!ValueTransformer.class.isAssignableFrom(transformer.getClass())) {
                    LOG.error(
                        "Could add global transformer [{}] to global registry. Type does not extend ValueTransformer",
                        transformer.getClass().getCanonicalName());
                } else {

                    // get the type that is bound to the transformer:
                    final Class<? extends Object> valueTransformerClass = transformer.getClass();

                    final Method method = ReflectionUtils.findMethod(valueTransformerClass, "unmarshalFromDb");
                    if (method != null) {
                        final Class<?> valueTransformerReturnType = method.getReturnType();
                        GlobalValueTransformerRegistry.register(valueTransformerReturnType,
                            (ValueTransformer<?, ?>) transformer);
                        LOG.debug("Global Value Transformer [{}] for type [{}] registered. ",
                            valueTransformerClass.getSimpleName(), valueTransformerReturnType.getSimpleName());
                    } else {
                        LOG.error(
                            "Could add global transformer [{}] to global registry. Could not find method unmarshalFromDb.",
                            valueTransformerClass);
                    }
                }
            }
        } else {
            LOG.warn("Could not find spring application context. No global ValueTransformer registered.");
        }
    }
}
