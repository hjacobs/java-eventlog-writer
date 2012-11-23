package de.zalando.zomcat.wsclient;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author  hjacobs
 */
public class WebServiceClientFactoryBeanTest {

    public interface ExampleWebService {
        void exampleMethod();
    }

    @Test
    public void test() {
        final WebServiceClientFactoryBean<ExampleWebService> fb =
            new WebServiceClientFactoryBean<WebServiceClientFactoryBeanTest.ExampleWebService>() {

                @Override
                public Class<ExampleWebService> getWebServiceClass() {
                    return ExampleWebService.class;
                }
            };
        Assert.assertEquals(ExampleWebService.class, fb.getObjectType());
        Assert.assertTrue(fb.isSingleton());

        // TODO: really test WS creation
    }

}
