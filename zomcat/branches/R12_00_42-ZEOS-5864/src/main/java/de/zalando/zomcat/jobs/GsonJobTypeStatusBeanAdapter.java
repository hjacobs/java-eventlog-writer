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
 * gson adapter for {@link JobTypeStatusBean JobTypeStatusBean}.
 *
 * @author  fbrick
 */
public class GsonJobTypeStatusBeanAdapter implements InstanceCreator<JobTypeStatusBean>,
    JsonSerializer<JobTypeStatusBean>, JsonDeserializer<JobTypeStatusBean> {

    @Override
    public JobTypeStatusBean createInstance(final Type type) {
        throw new UnsupportedOperationException("NOT implemeted yet");
    }

    @Override
    public JsonElement serialize(final JobTypeStatusBean src, final Type typeOfSrc,
            final JsonSerializationContext context) {
        final Map<String, Object> map = new HashMap<String, Object>();

        map.put("className", src.getJobClass().getName());
        map.put("runningWorker", src.getRunningWorker());

        final DateTime lastModified = src.getLastModified();

        map.put("manuallyPaused", src.isDisabled());
        map.put("activatedInConfig", src.getJobConfig().isActive());

        if (lastModified == null) {
            map.put("lastModified", null);
        } else {
            map.put("lastModified", lastModified.getMillis());
        }

        map.put("runningWorkers", src.getRunningWorkers());

        map.put("history", src.getHistory());

        return context.serialize(map);
    }

    @Override
    public JobTypeStatusBean deserialize(final JsonElement json, final Type typeOfT,
            final JsonDeserializationContext context) {
        throw new UnsupportedOperationException("NOT implemeted yet");
    }
}
