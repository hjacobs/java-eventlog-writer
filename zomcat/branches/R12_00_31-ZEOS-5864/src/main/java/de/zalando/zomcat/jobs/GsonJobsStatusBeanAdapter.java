package de.zalando.zomcat.jobs;

import java.lang.reflect.Type;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.InstanceCreator;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * gson adapter for {@link JobsStatusBean JobsStatusBean}.
 *
 * @author  fbrick
 */
public class GsonJobsStatusBeanAdapter implements InstanceCreator<JobsStatusBean>, JsonSerializer<JobsStatusBean>,
    JsonDeserializer<JobsStatusBean> {

    @Override
    public JobsStatusBean createInstance(final Type type) {
        return new JobsStatusBean();
    }

    @Override
    public JsonElement serialize(final JobsStatusBean src, final Type typeOfSrc,
            final JsonSerializationContext context) {
        final Map<String, Object> map = new HashMap<String, Object>();

        map.put("operationMode", src.getOperationModeAsEnum());
        map.put("jobs", src.getJobTypeStatusBeans());

        return context.serialize(map);
    }

    @Override
    public JobsStatusBean deserialize(final JsonElement json, final Type typeOfT,
            final JsonDeserializationContext context) {
        throw new UnsupportedOperationException("NOT implemeted yet");
    }
}
