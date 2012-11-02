package de.zalando.zomcat.flowid;

import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.zalando.utils.UUIDConverter;

public final class Injectors {

    private static final Logger LOG = LoggerFactory.getLogger(Injectors.class);

    private Injectors() { }

    public static final FlowIdDataInjector NO_OP_INJECTOR = new FlowIdDataInjector() {
        @Override
        public byte[] inject(final byte[] flowId, final FlowIdPayload payload) {
            return flowId;
        }

        @Override
        public FlowIdPayload extract(final byte[] flowId) {
            return null;
        }
    };

    public static final FlowIdDataInjector FRONTEND_DATA_INJECTOR = new FlowIdDataInjector() {
        @Override
        public byte[] inject(final byte[] flowId, final FlowIdPayload payload) {
            try {
                if (payload.getSessionPrefix() != null) {
                    final byte[] sessionBytes = UUIDConverter.getBytes(payload.getSessionPrefix());
                    System.arraycopy(sessionBytes, 0, flowId, 1, 5);
                }

                if (payload.getRemoteAddress() != null) {
                    final InetAddress inet = InetAddress.getByName(payload.getRemoteAddress());
                    System.arraycopy(inet.getAddress(), 0, flowId, 6, 4);
                }

                if (payload.getUserId() != null) {
                    setUserId(flowId, payload);
                } else {
                    setNullUserId(flowId);
                }
            } catch (final Exception e) {
                LOG.warn("Could not generate Flow-ID for frontend session " + payload.getSessionPrefix() + " from IP "
                        + payload.getRemoteAddress() + " for user " + payload.getUserId(), e);
            }

            return flowId;
        }

        @Override
        public FlowIdPayload extract(final byte[] flowId) {
            try {
                final byte[] sessionBytes = new byte[5];
                System.arraycopy(flowId, 1, sessionBytes, 0, 5);

                final byte[] inetBytes = new byte[4];
                System.arraycopy(flowId, 6, inetBytes, 0, 4);

                final InetAddress inet = InetAddress.getByAddress(inetBytes);
                final Integer userId = extractUserId(flowId);

                return new FlowIdPayload(UUIDConverter.getHexString(sessionBytes), inet.getHostAddress(), userId);
            } catch (final Exception e) {
                LOG.warn("Could not extract session prefix and remote address from Flow-ID " + flowId, e);
            }

            return null;
        }
    };

    public static final FlowIdDataInjector GWT_DATA_INJECTOR = new FlowIdDataInjector() {
        @Override
        public byte[] inject(final byte[] flowId, final FlowIdPayload payload) {
            if (payload.getUserId() != null) {
                setUserId(flowId, payload);
            } else {
                setNullUserId(flowId);
            }

            return flowId;
        }

        @Override
        public FlowIdPayload extract(final byte[] flowId) {
            try {
                final Integer userId = extractUserId(flowId);
                return new FlowIdPayload(null, null, userId);
            } catch (final Exception e) {
                LOG.warn("Could not extract session prefix and remote address from Flow-ID " + flowId, e);
            }

            return null;
        }

    };

    private static void setUserId(final byte[] flowId, final FlowIdPayload payload) {
        flowId[10] = (byte) (payload.getUserId() >>> 24);
        flowId[11] = (byte) (payload.getUserId() >>> 16);
        flowId[12] = (byte) (payload.getUserId() >>> 8);
        flowId[13] = payload.getUserId().byteValue();
    }

    private static void setNullUserId(final byte[] flowId) {
        flowId[10] = 0;
        flowId[11] = 0;
        flowId[12] = 0;
        flowId[13] = 0;
    }

    private static Integer extractUserId(final byte[] flowId) {
        Integer userId = 0;
        userId |= flowId[10] & 0xFF;
        userId <<= 8;
        userId |= flowId[11] & 0xFF;
        userId <<= 8;
        userId |= flowId[12] & 0xFF;
        userId <<= 8;
        userId |= flowId[13] & 0xFF;

        if (userId <= 0) {
            userId = null;
        }

        return userId;
    }
}
