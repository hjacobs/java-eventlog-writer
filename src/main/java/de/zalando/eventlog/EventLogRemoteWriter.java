package de.zalando.eventlog;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.concurrent.LinkedTransferQueue;

/**
 * Created by jmussler on 8/11/15.
 */
public class EventLogRemoteWriter implements Runnable {

    private static final Logger LOG = Logger.getLogger(RemoteEventLogger.class);

    private final String url;
    private final String oauthAccessTokenUrl;

    public final String applicationId;
    public final String applicationVersion;

    private long lastError = 0;

    private final ObjectMapper mapper;

    private final LinkedTransferQueue<EventTask> queue;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss,SSSXXX");

    public EventLogRemoteWriter(String url, String applicationId, String applicationVersion, String oauthAccessTokenUrl, LinkedTransferQueue<EventTask> queue) {
        this.url = url;
        this.oauthAccessTokenUrl = oauthAccessTokenUrl;
        this.queue = queue;
        this.applicationId = applicationId;
        this.applicationVersion = applicationVersion;
        this.mapper = new ObjectMapper();
    }

    private void doSleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void writeEvents() {
        while(true) {

            final long current = System.currentTimeMillis();

            if(current - lastError <= 2*1000) {
                doSleep(1000);
                continue;
            }

            EventTask task = queue.poll();

            if(null!=task) {

                if(task.getErrorCounter()==2 && (current - lastError < 5000)) {
                    doSleep(30*1000);
                    retry(task);
                    continue;
                }

                Event event = new Event();
                event.setApp_id(applicationId);
                event.setApp_version(applicationVersion);
                event.setTime(dateFormat.format(task.getTime()));
                event.setFields(task.getType().getFieldNames());
                event.setValues(task.getValues());
                event.setFlow_id(task.getFlowId());

                if(task.getErrorCounter()>=3) {
                    LOG.error("Could not write EventLog entry: " + event);
                    continue;
                }

                Executor ex = Executor.newInstance();
                String value = null;

                try {
                    value = mapper.writeValueAsString(event);
                    HttpResponse r = ex.execute(Request.Put(url).useExpectContinue().bodyString(value, ContentType.APPLICATION_JSON)).returnResponse();
                } catch (JsonProcessingException e) {
                    // not much we can do here
                    LOG.error("Failed to serialize: " + event, e);
                } catch (ClientProtocolException e) {
                    LOG.error("Client Protocol Error during write: " + event, e);
                    retry(task);
                } catch (IOException e) {
                    LOG.error("IOException during write: " + event, e);
                    retry(task);
                }
            }

            if(queue.isEmpty()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                }
            }
        }
    }

    public void retry(EventTask task) {
        task.incErrorCounter();
        task.setLastError();
        queue.add(task);
    }

    @Override
    public void run() {
        try {
            writeEvents();
        }
        catch(Exception ex) {

        }
    }
}
