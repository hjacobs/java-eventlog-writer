package de.zalando.zomcat;

import java.io.File;

import java.nio.charset.Charset;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.google.common.io.Files;

/**
 * static class to access the current CMDB host status (which will be persisted by cmdb-agent.py into /etc/host-status).
 *
 * @author  hjacobs
 */
public class HostStatus {

    public static final String ALLOCATED = "ALLOCATED";
    private static final String HOST_STATUS_FILE = "/etc/host-status";
    private static final long TTL_MILLIS = 20 * 1000; // 20 seconds

    private static final Pattern STATUS_PATTERN = Pattern.compile("^[A-Z][A-Z_]*[A-Z]$");

    private static String currentStatus;
    private static long lastUpdate;

    /**
     * get current host status as string.
     *
     * @return
     */
    public static String getStatusName() {
        long now = System.currentTimeMillis();
        if (currentStatus == null || lastUpdate < now - TTL_MILLIS) {
            synchronized (HOST_STATUS_FILE) {
                if (currentStatus == null || lastUpdate < now - TTL_MILLIS) {
                    String fileContents = null;
                    try {
                        fileContents = Files.toString(new File(HOST_STATUS_FILE), Charset.defaultCharset()).trim();
                    } catch (Exception e) { }

                    if (StringUtils.isNotBlank(fileContents) && STATUS_PATTERN.matcher(fileContents).matches()) {
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
     * @return  true if the host status is either unknown (null) or ALLOCATED
     */
    public static boolean isAllocated() {
        final String status = getStatusName();
        return status == null || ALLOCATED.equals(status);
    }

}
