package de.zalando.zomcat;

public interface SystemConstants {

    /**
     * the jvm process name defined as system property in wrapper.conf.
     */
    String SYSTEM_PROPERTY_NAME_JVM_PROCESS_NAME = "jvm.process.name";

    /**
     * the app instance key defined as system property in wrapper.conf.
     */
    String SYSTEM_PROPERTY_APP_INSTANCE_KEY = "app.instance.key";

    /**
     * the path to the file with credentials like DB passwords etc.
     */
    String SYSTEM_PROPERTY_CREDENTIALS_FILE = "zomcat.credentials.file";

    /**
     * the name of an instance running on a developer machine.
     */
    String SYSTEM_NAME_FOR_LOCAL_INSTANCE = "local_local";

}
