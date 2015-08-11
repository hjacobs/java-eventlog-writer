package de.zalando.eventlog;

/**
 * Created by jmussler on 1/13/15.
 */
import java.util.List;

public class Event {
    private String time;
    private int type_id;
    private List<String> fields;
    private List<String> values;
    private String flow_id;
    private String app_id;
    private String app_version;
    private String host_ip;

    @Override
    public String toString() {
        return "Event{" +
                "time='" + time + '\'' +
                ", type_id=" + type_id +
                ", fields=" + fields +
                ", values=" + values +
                ", flow_id='" + flow_id + '\'' +
                ", app_id='" + app_id + '\'' +
                ", app_version='" + app_version + '\'' +
                ", host_ip='" + host_ip + '\'' +
                '}';
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getType_id() {
        return type_id;
    }

    public void setType_id(int type_id) {
        this.type_id = type_id;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public String getFlow_id() {
        return flow_id;
    }

    public void setFlow_id(String flow_id) {
        this.flow_id = flow_id;
    }

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getApp_version() {
        return app_version;
    }

    public void setApp_version(String app_version) {
        this.app_version = app_version;
    }

    public String getHost_ip() {
        return host_ip;
    }

    public void setHost_ip(String host_ip) {
        this.host_ip = host_ip;
    }
}
