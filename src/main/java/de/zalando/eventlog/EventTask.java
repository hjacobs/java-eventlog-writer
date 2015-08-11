package de.zalando.eventlog;

import java.util.Date;
import java.util.List;

/**
 * Created by jmussler on 8/11/15.
 */
public class EventTask {
    private EventType type;
    private String flowId;
    private List<String> values;
    private Date time;
    private int errorCounter = 0;
    private long lastErrorTs = 0;

    public void setLastError() {
        lastErrorTs = System.currentTimeMillis();
    }

    public long getLastErrorTs() {
        return lastErrorTs;
    }

    public EventTask(EventType type, String flowId, List<String> values) {
        this.flowId = flowId;
        this.type = type;
        this.values = values;
        this.time = new Date();
    }

    public EventType getType() {
        return type;
    }

    public int getErrorCounter() {
        return errorCounter;
    }

    public void incErrorCounter() {
        errorCounter += 1;
    }

    public List<String> getValues() {
        return values;
    }

    public Date getTime() {
        return time;
    }

    public String getFlowId() {
        return flowId;
    }
}
