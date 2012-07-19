package de.zalando.zomcat.flowid;

import java.util.UUID;

import org.apache.log4j.Logger;
import org.apache.log4j.NDC;

import org.junit.Test;

import de.zalando.utils.UUIDConverter;

import junit.framework.Assert;

public class FlowIdTest {
    private static final String TEST_FLOW_ID = "test flow id.";
    private static final String TEST_FLOW_ID_2 = "test flow id 2.";

    private static final Logger LOG = Logger.getLogger(FlowIdTest.class);

    @Test
    public void testJobFlowId() throws Exception {
        LOG.debug("Message with empty flowId");

        Assert.assertEquals("There should be no diagnostic messages defined.", 0, NDC.getDepth());

        FlowId.pushFlowId(TEST_FLOW_ID);
        Assert.assertEquals("There should be one diagnostic messages defined.", 1, NDC.getDepth());

        LOG.debug("Message with generated UUID as flowId");

        final String lastFlowId = FlowId.popFlowId();
        Assert.assertEquals(lastFlowId, TEST_FLOW_ID);
        Assert.assertEquals("There should be no diagnostic messages defined.", 0, NDC.getDepth());

        LOG.debug("Message without UUID as flowId - removed flowId.");
    }

    @Test
    public void testNestedJobFlowId() throws Exception {
        LOG.debug("Message with empty flowId");

        Assert.assertEquals("There should be no diagnostic messages defined.", 0, NDC.getDepth());

        FlowId.pushFlowId(TEST_FLOW_ID);
        Assert.assertEquals("There should be one diagnostic messages defined.", 1, NDC.getDepth());

        LOG.debug("Message with one generated UUID as flowId");

        FlowId.pushFlowId(TEST_FLOW_ID_2);
        Assert.assertEquals("There should be two diagnostic messages defined.", 2, NDC.getDepth());

        LOG.debug("Message with two generated UUID as flowId");

        String lastFlowId = FlowId.popFlowId();
        Assert.assertEquals(lastFlowId, TEST_FLOW_ID_2);

        LOG.debug("Message with one generated UUID as flowId");

        lastFlowId = FlowId.popFlowId();
        Assert.assertEquals(lastFlowId, TEST_FLOW_ID);

        Assert.assertEquals("There should be no diagnostic messages defined.", 0, NDC.getDepth());

        LOG.debug("Message without UUID as flowId - removed flowId.");
    }

    @Test
    public void testFlowIdGeneration() throws Exception {
        final String flowId = FlowId.generateFlowId();
        Assert.assertEquals(22, flowId.length());
        Assert.assertTrue(flowId.startsWith("R"));

        FlowId.generateAndPushFlowId();

        String lastFlowId = FlowId.peekFlowId();
        Assert.assertNotNull(lastFlowId);
        lastFlowId = FlowId.popFlowId();
        Assert.assertNotNull(lastFlowId);
        lastFlowId = FlowId.popFlowId();
        Assert.assertEquals(0, lastFlowId.length());
    }

    @Test
    public void testFlowIdWithPayloadEmptyUserId() {
        final String sessionId = UUIDConverter.getBase35String(UUID.randomUUID());
        final String flowId = FlowId.generateFlowIdWithPayload(sessionId, "127.0.0.1", null);
        Assert.assertEquals(22, flowId.length());
        Assert.assertTrue(flowId.startsWith("F"));

        final FlowIdPayload payload = FlowId.extractPayload(flowId);
        Assert.assertEquals(UUIDConverter.getAlternativeRepresentation(sessionId).replace("-", "").substring(0, 10),
            payload.getSessionPrefix());
        Assert.assertEquals("127.0.0.1", payload.getRemoteAddress());
        Assert.assertNull(payload.getUserId());
    }

    @Test
    public void testFlowIdWithPayload() {
        final String sessionId = UUIDConverter.getBase35String(UUID.randomUUID());
        final String sessionIdAsHex = getSessionIdAsHex(sessionId);
        final String customerNumber = "3012267130";
        final int userId = Integer.valueOf(customerNumber.substring(1, 9));
        final String ip = "202.179.16.172";
        final String flowId = FlowId.generateFlowIdWithPayload(sessionId, ip, userId);
        Assert.assertEquals(22, flowId.length());
        Assert.assertTrue(flowId.startsWith("U"));

        final FlowIdPayload payload = FlowId.extractPayload(flowId);
        Assert.assertEquals(sessionIdAsHex.substring(0, 10), payload.getSessionPrefix());
        Assert.assertEquals(ip, payload.getRemoteAddress());
        Assert.assertEquals(userId, (int) payload.getUserId());
    }

    @Test
    public void testFlowIdWithPayload2() {
        final String sessionId = UUIDConverter.getBase35String(UUID.randomUUID());
        final String sessionIdAsHex = getSessionIdAsHex(sessionId);
        final String customerNumber = "93000033002";
        final int userId = Integer.valueOf(customerNumber.substring(2, 9));
        final String ip = "41.71.144.8";
        final String flowId = FlowId.generateFlowIdWithPayload(sessionId, ip, userId);
        Assert.assertEquals(22, flowId.length());
        Assert.assertTrue(flowId.startsWith("U"));

        final FlowIdPayload payload = FlowId.extractPayload(flowId);
        Assert.assertEquals(sessionIdAsHex.substring(0, 10), payload.getSessionPrefix());
        Assert.assertEquals(ip, payload.getRemoteAddress());
        Assert.assertEquals(userId, (int) payload.getUserId());
    }

    @Test
    public void testFlowIdWithPayload3() {
        final String userNumber = "12345";
        final int userId = Integer.valueOf(userNumber.substring(2, 5));
        final String flowId = FlowId.generateFlowIdWithPayload(FlowIdType.GWT, null, null, userId);
        Assert.assertEquals(22, flowId.length());
        Assert.assertTrue(flowId.startsWith("G"));

        final FlowIdPayload payload = FlowId.extractPayload(flowId);
        Assert.assertEquals(userId, (int) payload.getUserId());
    }

    @Test
    public void testFlowIdWithPayload4() {
        final String sessionId = UUIDConverter.getBase35String(UUID.randomUUID());
        final String sessionIdAsHex = getSessionIdAsHex(sessionId);
        final String ip = "41.71.144.8";
        final String flowId = FlowId.generateFlowIdWithPayload(FlowIdType.FRONTEND, sessionId, ip, null);
        Assert.assertEquals(22, flowId.length());
        Assert.assertTrue(flowId.startsWith("F"));

        final FlowIdPayload payload = FlowId.extractPayload(flowId);
        Assert.assertEquals(sessionIdAsHex.substring(0, 10), payload.getSessionPrefix());
        Assert.assertEquals(ip, payload.getRemoteAddress());
    }

    @Test
    public void testFlowIdWithPayload5() {
        final String flowId = FlowId.generateFlowIdWithPayload(FlowIdType.JOB, null, null, null);
        Assert.assertEquals(22, flowId.length());
        Assert.assertTrue(flowId.startsWith("J"));
    }

    @Test
    public void testFlowIdWithPayload6() {
        final String flowId = FlowId.generateFlowIdWithPayload(FlowIdType.RANDOM, null, null, null);
        Assert.assertEquals(22, flowId.length());
        Assert.assertTrue(flowId.startsWith("R"));
    }

    @Test
    public void testFlowIdWithPayload7() {
        final String sessionId = UUIDConverter.getBase35String(UUID.randomUUID());
        final String sessionIdAsHex = getSessionIdAsHex(sessionId);
        final String customerNumber = "93000033002";
        final int userId = Integer.valueOf(customerNumber.substring(2, 9));
        final String ip = "41.71.144.8";
        final String flowId = FlowId.generateFlowIdWithPayload(FlowIdType.USER, sessionId, ip, userId);
        Assert.assertEquals(22, flowId.length());
        Assert.assertTrue(flowId.startsWith("U"));

        final FlowIdPayload payload = FlowId.extractPayload(flowId);
        Assert.assertEquals(sessionIdAsHex.substring(0, 10), payload.getSessionPrefix());
        Assert.assertEquals(ip, payload.getRemoteAddress());
        Assert.assertEquals(userId, (int) payload.getUserId());
    }

    private String getSessionIdAsHex(final String sessionId) {
        final String sessionIdAsHex = UUIDConverter.getAlternativeRepresentation(sessionId).replace("-", "");
        return sessionIdAsHex;
    }

}
