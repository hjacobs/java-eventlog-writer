package de.zalando.zomcat.jobs;

import java.lang.reflect.Type;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;

import com.google.gson.InstanceCreator;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * gson adapter for {@link RunningWorker RunningWorker}.
 *
 * @author  fbrick
 */
public class GsonRunningWorkerBeanAdapter implements InstanceCreator<RunningWorkerBean>,
    JsonSerializer<RunningWorkerBean>, JsonDeserializer<RunningWorkerBean> {

    @Override
    public RunningWorkerBean createInstance(final Type type) {
        throw new UnsupportedOperationException("NOT implemeted yet");
    }

    @Override
    public JsonElement serialize(final RunningWorkerBean src, final Type typeOfSrc,
            final JsonSerializationContext context) {
        final Map<String, Object> map = new HashMap<String, Object>();

        map.put("id", src.getId());

        final DateTime internalStartTime = src.getInternalStartTime();

        if (internalStartTime == null) {
            map.put("jobStartTime", null);
        } else {
            map.put("jobStartTime", internalStartTime.getMillis());
        }

        map.put("actualProcessedItemNumber", src.getActualProcessedItemNumber());
        map.put("totalNumberOfItemsToBeProcessed", src.getTotalNumberOfItemsToBeProcessed());

        return context.serialize(map);
    }

    @Override
    public RunningWorkerBean deserialize(final JsonElement json, final Type typeOfT,
            final JsonDeserializationContext context) {
        throw new UnsupportedOperationException("NOT implemeted yet");
    }
}
