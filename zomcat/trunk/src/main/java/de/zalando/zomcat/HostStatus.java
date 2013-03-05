package de.zalando.zomcat;

import java.io.File;

import java.nio.charset.Charset;

import org.apache.commons.lang.StringUtils;

import com.google.common.io.Files;

/**
 * @author  hjacobs
 */
public class HostStatus {

    public static final String ALLOCATED = "ALLOCATED";
    private static final String HOST_STATUS_FILE = "/etc/host-status";
    private static final long ONE_MINUTE_MILLIS = 60 * 1000;

    private static String currentStatus;
    private static long lastUpdate;

    /**
     * get current host status as string.
     *
     * @return
     */
    public static String getStatusName() {
        long now = System.currentTimeMillis();
        if (currentStatus == null || lastUpdate < now - ONE_MINUTE_MILLIS) {
            synchronized (HOST_STATUS_FILE) {
                if (currentStatus == null || lastUpdate < now - ONE_MINUTE_MILLIS) {
                    String fileContents = null;
                    try {
                        fileContents = Files.toString(new File(HOST_STATUS_FILE), Charset.defaultCharset());
                    } catch (Exception e) { }

                    if (StringUtils.isNotBlank(fileContents)) {
                        currentStatus = fileContents;
                    }

                    lastUpdate = now;
                }
            }
        }

        return currentStatus;
    }

    /**
     * convenience method to check if the current host status is "ALLOCATED" (The host is in what should likely be
     * considered a production state.).
     *
     * @return
     */
    public static boolean isAllocated() {
        final String status = getStatusName();
        return status == null || ALLOCATED.equals(status);
    }

}
