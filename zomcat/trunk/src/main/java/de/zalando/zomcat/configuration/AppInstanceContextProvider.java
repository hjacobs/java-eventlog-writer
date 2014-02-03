package de.zalando.zomcat.configuration;

import java.io.IOException;
import java.io.InputStream;

import java.net.UnknownHostException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

import de.zalando.domain.DataCenter;
import de.zalando.domain.Environment;

import de.zalando.zomcat.SystemConstants;

/**
 * This class is trying to gather information about the application from the MANIFEST.MF file. The information in
 * MANIFEST.MF is written by DeployCtl, so this class will only return nulls locally
 *
 * @author  mjuenemann
 */
public class AppInstanceContextProvider implements AppInstanceKeySource {

    private static enum Context {
        PROJECT_ID("zomcat.project", "X-Project"),
        ENVIRONMENT("zomcat.environment", "X-Environment"),
        DATA_CENTER("zomcat.dataCenter", "X-DataCenter"),
        VERSION("zomcat.implementationTag", "Implementation-Tag");

        private final String systemProperty;
        private final String manifestKey;

        private Context(final String systemProperty, final String manifestKey) {
            this.systemProperty = systemProperty;
            this.manifestKey = manifestKey;
        }

        /**
         * Tries to retrieve the actual value for the context from the system properties. If it was not found there, it
         * tries to read it from the given manifest
         *
         * @param   manifest  The manifest written by deployctl. May be null locally
         *
         * @return  The value or null if the value could not be retrieved
         */
        public String retrieveValue(final Manifest manifest) {
            String value = System.getProperty(systemProperty);

            if (value == null && manifest != null) {
                value = manifest.getMainAttributes().getValue(manifestKey);
            }

            return value;
        }
    }

    private static final Logger LOG = LoggerFactory.getLogger(AppInstanceContextProvider.class);
    private static final String PROJECT_SEPARATOR = ":";
    private static final Pattern SEGMENT_FROM_HOST_PATTERN = Pattern.compile("[0-9]+$");
    private static final int SEGMENT_COUNT = 3;

    private final String host;
    private final Integer segment;
    private final String instanceCode;
    private final String projectId;
    private final String projectName;
    private final String version;
    private final Environment environment;
    private final DataCenter dataCenter;

    /**
     * This constructor will try to determine the contexts from the system properties. If it can't find a property
     * there, it will look for it in the given manifest.
     *
     * @param       host          The host we are currently running on
     * @param       instanceCode  The deployctl code of the current instance. If it has the format p1234 it will be
     *                            converted to 1234. May be null locally
     * @param       manifest      The manifest written by deployctl. May be null locally
     *
     * @deprecated  This constructor should be private. Use one of the public factory methods to construct this object,
     *              for example .fromManifestOnFileystem
     */
    @Deprecated
    public AppInstanceContextProvider(final String host, final String instanceCode, final Manifest manifest) {
        this(host, instanceCode, Context.PROJECT_ID.retrieveValue(manifest), Context.VERSION.retrieveValue(manifest),
            Context.ENVIRONMENT.retrieveValue(manifest), Context.DATA_CENTER.retrieveValue(manifest));
    }

    /**
     * This constructor determines the contexts solely from the given parameters. It does not access system properties
     * or the manifest.
     *
     * @param  host             The host we are currently running on
     * @param  instanceCode     The deployctl code of the current instance. If it has the format p1234 it will be
     *                          converted to 1234
     * @param  projectId        The current project's id in the format groupId:artifactId. The artifactId will be
     *                          extracted and used as project name
     * @param  version          The version of the current instance
     * @param  environmentCode  The current environment in deployctl format, e.g. "release-staging", "live". It will be
     *                          converted to the de.zalando.domain.Environment enum and an error will be logged if the
     *                          conversion fails
     * @param  dataCenterCode   The data center which the instance is running in. If the code can not be found in the
     *                          de.zalando.domain.DataCenter enum, an error will be logged
     */
    private AppInstanceContextProvider(final String host, final String instanceCode, final String projectId,
            final String version, final String environmentCode, final String dataCenterCode) {

        // Host and Segment
        Integer segment = null;
        if (host != null) {
            final Matcher matcher = SEGMENT_FROM_HOST_PATTERN.matcher(host);
            if (matcher.find()) {
                segment = (Integer.valueOf(matcher.group()) - 1) % SEGMENT_COUNT + 1;
            }
        }

        this.segment = segment;
        this.host = host;

        // InstanceCode (p3600 => 3600)
        if (instanceCode != null && instanceCode.length() == 5) {
            this.instanceCode = instanceCode.substring(1);
        } else if (instanceCode != null && instanceCode.length() == 4) {
            this.instanceCode = instanceCode;
        } else {
            this.instanceCode = null;
        }

        // ProjectId and ProjectName
        int pos;
        this.projectId = projectId;
        if (projectId != null && (pos = projectId.indexOf(PROJECT_SEPARATOR)) > -1) {
            this.projectName = projectId.substring(pos + 1);
        } else {
            this.projectName = null;
        }

        // Environment, DataCenter, Version
        Environment environment = null;
        if (environmentCode != null) {
            try {
                environment = Environment.valueOf(environmentCode.toUpperCase().replace('-', '_'));
            } catch (IllegalArgumentException e) {
                LOG.error("DeployCtl X-Environment doesn't match Environment enum", e);
            }
        }

        DataCenter dataCenter = null;
        if (dataCenterCode != null) {
            try {
                dataCenter = DataCenter.valueOf(dataCenterCode);
            } catch (IllegalArgumentException e) {
                LOG.error("DeployCtl X-DataCenter doesn't match Environment enum", e);
            }
        }

        this.environment = environment;
        this.dataCenter = dataCenter;
        this.version = version;
    }

    public String getHost() {
        return host;
    }

    public Integer getSegment() {
        return segment;
    }

    public String getInstanceCode() {
        return instanceCode;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getVersion() {
        return version;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public DataCenter getDataCenter() {
        return dataCenter;
    }

    /**
     * Get the AppInstanceKey from the system environment. Moved here from BaseApplicaionConfigImpl
     *
     * @return  String
     */
    @Override
    public String getAppInstanceKey() {
        final String key = SystemConstants.SYSTEM_PROPERTY_APP_INSTANCE_KEY;

        String appInstanceKey = System.getProperty(key);

        if (appInstanceKey == null) {

            final String defaultValue = SystemConstants.SYSTEM_NAME_FOR_LOCAL_INSTANCE;

            final String exclamation = Strings.repeat("!", 120);
            final StringBuilder builder = new StringBuilder(300);
            builder.append('\n').append(exclamation);
            builder.append("\nNo App Instance Key found, setting " + key + " = " + defaultValue);
            builder.append('\n').append(exclamation);
            LOG.error(builder.toString());

            System.setProperty(key, defaultValue);

            appInstanceKey = defaultValue;

        }

        return appInstanceKey;
    }

    /**
     * Tries to read the MANIFEST.MF from the local filesystem using it's absolute path
     * (/data/zalando/processes/{jvm.process.name}/ROOT/META-INF/MANIFEST.MF).
     *
     * @return  AppInstanceContextProvider
     */
    public static AppInstanceContextProvider fromManifestOnFilesystem() {
        final String instanceCode = System.getProperty(SystemConstants.SYSTEM_PROPERTY_NAME_JVM_PROCESS_NAME);
        String host;

        try {
            host = java.net.InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            LOG.debug("Unable to get hostname", e);
            host = null;
        }

        if (instanceCode != null) {
            final Path path = Paths.get(String.format(SystemConstants.SYSTEM_PATH_TO_MANIFEST, instanceCode));
            try {
                final InputStream is = Files.newInputStream(path);
                return new AppInstanceContextProvider(host, instanceCode, new Manifest(is));
            } catch (IOException e) {
                LOG.debug("Unable to read MANIFEST.MF from filesystem, should only happen locally {}", path.toString());
            }
        }

        return new AppInstanceContextProvider(host, instanceCode, null);
    }

}
