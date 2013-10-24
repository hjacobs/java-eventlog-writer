package de.zalando.spring.context;

import org.junit.Test;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ContextTest {

    @Test
    public void mergeApplicationContext() {
        final AnnotationConfigApplicationContext rootContext = new AnnotationConfigApplicationContext();
        rootContext.register(AppRootContext.class);
        rootContext.setAllowBeanDefinitionOverriding(false);
        rootContext.refresh();

        final AnnotationConfigApplicationContext servletContext = new AnnotationConfigApplicationContext();
        servletContext.setParent(rootContext);
        servletContext.setAllowBeanDefinitionOverriding(false);
        servletContext.register(AppServletContext.class);
        servletContext.refresh();

        final Singleton one = (Singleton) rootContext.getBean("singleton");
        final Singleton andAnotherOne = (Singleton) servletContext.getBean("singleton");

        System.out.println("Singletons are " + (one.id.equals(andAnotherOne.id) ? "equal" : "different"));
    }

}
