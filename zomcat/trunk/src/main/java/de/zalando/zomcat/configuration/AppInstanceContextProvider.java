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

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

import de.zalando.domain.Environment;

import de.zalando.zomcat.SystemConstants;

/**
 * This class is trying to gather information about the application from the MANIFEST.MF file which is assumed to be on
 * the classpath. The information is written to MANIFEST.MF by DeployCtl, so this class will only return nulls locally
 *
 * @author  mjuenemann
 */
public class AppInstanceContextProvider implements AppInstanceKeySource {

    private static final Logger LOG = LoggerFactory.getLogger(AppInstanceContextProvider.class);

    private static final String MANIFEST_PATH = "/META-INF/MANIFEST.MF";
    private static final String VERSION_KEY = "Implementation-Tag";
    private static final String ENVIRONMENT_KEY = "X-Environment";
    private static final String PROJECT_KEY = "X-Project";
    private static final String PROJECT_SEPARATOR = ":";
    private static final Pattern SEGMENT_FROM_HOST_PATTERN = Pattern.compile("[0-9]+$");
    private static final int SEGMENT_COUNT = 3;

    private final String host;
    private final String instanceCode;
    private final Manifest manifest;

    public AppInstanceContextProvider(final String host, final String instanceCode, final Manifest manifest) {
        this.host = host;
        this.manifest = manifest;

        if (instanceCode != null && instanceCode.length() == 5) {
            this.instanceCode = instanceCode.substring(1); // p3600 => 3600
        } else if (instanceCode != null && instanceCode.length() == 4) {
            this.instanceCode = instanceCode;
        } else {
            this.instanceCode = null;
        }
    }

    /**
     * @return  String Project id (e.g. de.zalando:visualmatchingengine-backend) or null if it could not be read from
     *          the MANIFEST (locally for example)
     */
    public String getProjectId() {
        if (manifest != null) {
            return manifest.getMainAttributes().getValue(PROJECT_KEY);
        }

        return null;
    }

    /**
     * @return  String Project name (e.g. visualmatchingengine-backend) or null if it could not be read from the
     *          MANIFEST (locally for example)
     */
    public String getProjectName() {
        if (manifest != null) {

            final String project = getProjectId();
            final int pos = project.indexOf(PROJECT_SEPARATOR);
            if (pos > -1) {
                return project.substring(pos + 1);
            }

            return project;
        } else {
            return null;
        }
    }

    /**
     * @return  String Instance code (e.g. "9620") or null if it could not be read from the MANIFEST (locally for
     *          example)
     */
    public String getInstanceCode() {
        return instanceCode;
    }

    /**
     * @return  String Hostname of the current machine
     */
    public String getHost() {
        return host;
    }

    /**
     * @return  Integer Segment of the current instance. Is simple calculated by taking the number at the end of the
     *          hostname and taking it modulo 3, so it's not really reliable
     */
    public Integer getSegment() {
        Matcher matcher = SEGMENT_FROM_HOST_PATTERN.matcher(getHost());
        if (matcher.find()) {
            return (Integer.valueOf(matcher.group()) - 1) % SEGMENT_COUNT + 1;
        }

        return null;
    }

    /**
     * @return  Environment Environment (e.g. RELEASE_STAGING) or null if it could not be read from the MANIFEST
     *          (locally for example)
     */
    public Environment getEnvironment() {
        if (manifest != null) {
            final String env = manifest.getMainAttributes().getValue(ENVIRONMENT_KEY);
            if (env != null) {
                try {
                    return Environment.valueOf(env.toUpperCase().replace('-', '_'));
                } catch (IllegalArgumentException e) {
                    LOG.error("DeployCtl X-Environment doesn't match Environment enum", e);
                }
            }
        }

        return null;
    }

    /**
     * @return  String Version (e.g. "R13_00_24-SHOP-001") or null if it could not be read from the MANIFEST (locally
     *          for example)
     */
    public String getVersion() {
        if (manifest != null) {
            return manifest.getMainAttributes().getValue(VERSION_KEY);
        } else {
            return null;
        }
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
     * Tries to open the MANIFEST.MF via the given ServletContext.
     *
     * @return  AppInstanceContextProvider
     */
    @Deprecated
    public static AppInstanceContextProvider fromServletContext(final ServletContext context) {
        final String instanceCode = System.getProperty(SystemConstants.SYSTEM_PROPERTY_NAME_JVM_PROCESS_NAME);
        String host;

        try {
            host = java.net.InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            LOG.debug("Unable to get hostname", e);
            host = null;
        }

        if (context != null) {
            try {
                final InputStream is = context.getResourceAsStream(MANIFEST_PATH);
                if (is != null) {
                    return new AppInstanceContextProvider(host, instanceCode, new Manifest(is));
                } else {
                    LOG.debug("Unable to read META-INF/MANIFEST.MF, probably running locally");
                }
            } catch (IOException e) {
                LOG.debug("Unable to read META-INF/MANIFEST.MF", e);
            }
        }

        return new AppInstanceContextProvider(host, instanceCode, null);
    }

    /**
     * Tries to get the ServletContext via Spring's ContextLoader. Will not work locally or in unit tests
     *
     * @return  AppInstanceContextProvider
     */
    @Deprecated
    public static AppInstanceContextProvider fromSpringWebApplicationContext() {
        return fromManifestOnFilesystem();
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
