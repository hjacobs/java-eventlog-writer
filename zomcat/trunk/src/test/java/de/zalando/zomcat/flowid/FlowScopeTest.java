package de.zalando.zomcat.flowid;

import org.junit.Assert;
import org.junit.Test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class FlowScopeTest {
    private ApplicationContext newContext() {
        return new ClassPathXmlApplicationContext("flowScopeTests.xml");
    }

    @Test
    public void testServiceExists() {
        final ApplicationContext ctx = newContext();

        Assert.assertFalse("not active", FlowId.getScope().isActive());
        FlowId.getScope().enter();
        Assert.assertTrue("active", FlowId.getScope().isActive());

        final CounterService service = ctx.getBean(CounterService.class);
        Assert.assertNotNull(service);
        FlowId.getScope().exit();
        Assert.assertFalse("not active again", FlowId.getScope().isActive());
    }

    @Test(expected = Exception.class)
    public void testNoScopeActive() {
        final ApplicationContext ctx = newContext();

        Assert.assertFalse("not active", FlowId.getScope().isActive());
        ctx.getBean(CounterService.class);
    }

    @Test
    public void testScopeActivation() {
        Assert.assertFalse("not active", FlowId.getScope().isActive());
        FlowId.getScope().enter();
        Assert.assertTrue("active", FlowId.getScope().isActive());
        FlowId.getScope().exit();
        Assert.assertFalse("not active again", FlowId.getScope().isActive());
    }

    @Test
    public void testScopeActivationWithId() {
        Assert.assertFalse("not active", FlowId.getScope().isActive());
        FlowId.getScope().enter("myid");
        Assert.assertTrue("active", FlowId.getScope().isActive());

        Assert.assertEquals("flowId", "myid", FlowId.getScope().getConversationId());

        FlowId.getScope().exit("myid");
        Assert.assertFalse("not active again", FlowId.getScope().isActive());
    }

    @Test
    public void testScopeState() {
        final ApplicationContext ctx = newContext();

        FlowId.getScope().enter();

        final CounterService service1 = ctx.getBean(CounterService.class);
        service1.inc();
        service1.inc();
        Assert.assertEquals("counter service1", 2, service1.getCounter());

        final CounterService service2 = ctx.getBean(CounterService.class);
        Assert.assertEquals("counter service2", 2, service2.getCounter());

        FlowId.getScope().exit();
    }

    @Test
    public void testScopeStateInDifferentFlows() {
        final ApplicationContext ctx = newContext();

        FlowId.getScope().enter();

        final CounterService service1 = ctx.getBean(CounterService.class);
        service1.inc();
        service1.inc();
        Assert.assertEquals("counter service1", 2, service1.getCounter());

        FlowId.getScope().exit();
        FlowId.getScope().enter();

        final CounterService service2 = ctx.getBean(CounterService.class);
        Assert.assertEquals("counter service2", 0, service2.getCounter());

        FlowId.getScope().exit();
    }
}
