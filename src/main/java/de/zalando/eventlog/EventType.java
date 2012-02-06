package de.zalando.eventlog;

import java.util.List;

/**
 * @author  hjacobs
 */
public interface EventType {

    int getId();

    String getName();

    List<String> getFieldNames();

}
