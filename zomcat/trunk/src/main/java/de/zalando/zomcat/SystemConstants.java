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
     * the name of an instance running on a developer machine.
     */
    String SYSTEM_NAME_FOR_LOCAL_INSTANCE = "local_local";

    /**
     * absolute path to the MANIFEST.MF file written by deployctl.
     */
    String SYSTEM_PATH_TO_MANIFEST = "/data/zalando/processes/%s/ROOT/META-INF/MANIFEST.MF";
}
