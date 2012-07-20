package de.zalando.zomcat.flowid;

public interface FlowIdDataInjector {

    byte[] inject(final byte[] flowId, final FlowIdPayload payload);

    FlowIdPayload extract(byte[] flowId);

}
