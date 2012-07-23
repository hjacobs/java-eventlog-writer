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
 * gson adapter for {@link FinishedWorkerBean FinishedWorkerBean}.
 *
 * @author  fbrick
 */
public class GsonFinishedWorkerBeanAdapter implements InstanceCreator<FinishedWorkerBean>,
    JsonSerializer<FinishedWorkerBean>, JsonDeserializer<FinishedWorkerBean> {

    @Override
    public FinishedWorkerBean createInstance(final Type type) {
        throw new UnsupportedOperationException("NOT implemeted yet");
    }

    @Override
    public JsonElement serialize(final FinishedWorkerBean src, final Type typeOfSrc,
            final JsonSerializationContext context) {
        final Map<String, Object> map = new HashMap<String, Object>();

        map.put("id", src.getId());

        final DateTime internalStartTime = src.getInternalStartTime();

        if (internalStartTime == null) {
            map.put("jobStartTime", null);
        } else {
            map.put("jobStartTime", internalStartTime.getMillis());
        }

        final DateTime endTime = src.getEndTime();

        if (endTime == null) {
            map.put("jobEndTime", null);
        } else {
            map.put("jobEndTime", endTime.getMillis());
        }

        map.put("actualProcessedItemNumber", src.getActualProcessedItemNumber());
        map.put("totalNumberOfItemsToBeProcessed", src.getTotalNumberOfItemsToBeProcessed());

        return context.serialize(map);
    }

    @Override
    public FinishedWorkerBean deserialize(final JsonElement json, final Type typeOfT,
            final JsonDeserializationContext context) {
        throw new UnsupportedOperationException("NOT implemeted yet");
    }
}
