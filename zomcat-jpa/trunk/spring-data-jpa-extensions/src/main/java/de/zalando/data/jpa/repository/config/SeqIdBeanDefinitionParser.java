package de.zalando.data.jpa.repository.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;

import org.w3c.dom.Element;

public class SeqIdBeanDefinitionParser implements BeanDefinitionParser {

    static final String KEY_ENTITY_LISTENER_CLASS_NAME = "de.zalando.data.jpa.domain.support.SeqIdEntityListener";
    private static final String KEY_BFPP_CLASS_NAME =
        "de.zalando.data.jpa.domain.support.SeqIdBeanFactoryPostProcessor";

    private final BeanDefinitionParser seqIdHandlerParser = new SeqIdHandlerBeanDefinitionParser();

    public BeanDefinition parse(final Element element, final ParserContext parser) {

        new SpringConfiguredBeanDefinitionParser().parse(element, parser);

        BeanDefinition keyHandlerDefinition = seqIdHandlerParser.parse(element, parser);

        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(KEY_ENTITY_LISTENER_CLASS_NAME);

        builder.addPropertyValue("seqIdHandler", keyHandlerDefinition);
        builder.setScope("prototype");

        registerInfrastructureBeanWithId(builder.getRawBeanDefinition(), KEY_ENTITY_LISTENER_CLASS_NAME, parser,
            element);

        RootBeanDefinition def = new RootBeanDefinition(KEY_BFPP_CLASS_NAME);
        registerInfrastructureBeanWithId(def, KEY_BFPP_CLASS_NAME, parser, element);
        return null;
    }

    private void registerInfrastructureBeanWithId(final AbstractBeanDefinition def, final String id,
            final ParserContext context, final Element element) {

        def.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        def.setSource(context.extractSource(element));
        context.registerBeanComponent(new BeanComponentDefinition(def, id));
    }

    /**
     * Copied code of SpringConfiguredBeanDefinitionParser until this class gets public.
     *
     * @author  Juergen Hoeller
     * @see     http://jira.springframework.org/browse/SPR-7340
     */
    private static class SpringConfiguredBeanDefinitionParser implements BeanDefinitionParser {

        /**
         * The bean name of the internally managed bean configurer aspect.
         */
        private static final String BEAN_CONFIGURER_ASPECT_BEAN_NAME =
            "org.springframework.context.config.internalBeanConfigurerAspect";

        private static final String BEAN_CONFIGURER_ASPECT_CLASS_NAME =
            "org.springframework.beans.factory.aspectj.AnnotationBeanConfigurerAspect";

        public BeanDefinition parse(final Element element, final ParserContext parserContext) {

            if (!parserContext.getRegistry().containsBeanDefinition(BEAN_CONFIGURER_ASPECT_BEAN_NAME)) {
                RootBeanDefinition def = new RootBeanDefinition();
                def.setBeanClassName(BEAN_CONFIGURER_ASPECT_CLASS_NAME);
                def.setFactoryMethodName("aspectOf");

                def.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
                def.setSource(parserContext.extractSource(element));
                parserContext.registerBeanComponent(new BeanComponentDefinition(def, BEAN_CONFIGURER_ASPECT_BEAN_NAME));
            }

            return null;
        }
    }
}
