package de.zalando.zomcat.flowid;

import static de.zalando.zomcat.flowid.Injectors.FRONTEND_DATA_INJECTOR;
import static de.zalando.zomcat.flowid.Injectors.GWT_DATA_INJECTOR;
import static de.zalando.zomcat.flowid.Injectors.NO_OP_INJECTOR;

public enum FlowIdType {
    JOB((byte) 36, NO_OP_INJECTOR),
    RANDOM((byte) 71, NO_OP_INJECTOR),
    FRONTEND((byte) 23, FRONTEND_DATA_INJECTOR),
    USER((byte) 80, FRONTEND_DATA_INJECTOR),
    GWT((byte) 24, GWT_DATA_INJECTOR);

    private byte charCode;

    private FlowIdDataInjector injector;

    private FlowIdType(final byte charCode, final FlowIdDataInjector injector) {
        this.charCode = charCode;
        this.injector = injector;
    }

    public byte getCharCode() {
        return charCode;
    }

    public FlowIdDataInjector getInjector() {
        return injector;
    }

    public boolean hasInjectedData() {
        return injector != NO_OP_INJECTOR;
    }

    public static FlowIdType valueOf(final byte byteCode) {
        for (FlowIdType type : FlowIdType.values()) {
            if (type.getCharCode() == byteCode) {
                return type;
            }
        }

        return null;
    }
}
