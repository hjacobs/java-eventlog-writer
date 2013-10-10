package de.zalando.zomcat.logging;

import java.io.Serializable;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

/**
 * A JMX Bean that configuration of the Log4J logging system at runtime.
 *
 * <p>Through this component it is possible at runtime to configure the logging level for a given category, whether it's
 * defined or not (it is created if it doesn't exist).
 *
 * <p>Note that configurations executed with this Bean are not persistent, i.e. they won't be written to <code>
 * log4j.xml</code> or any other form of persistence.
 *
 * @author  rreis
 */
@Component("log4jConfigurationBean")
@ManagedResource(objectName = "Zalando:name=Log4J Configuration Bean")
public class Log4jMBean implements LoggingMBean, Serializable {

    /**
     * The serial version UID of this class.
     */
    private static final long serialVersionUID = -2666093866809006322L;

    /**
     * The bean name of this class.
     */
    public static final String BEAN_NAME = "log4jConfigurationBean";

    /**
     * Sets the logger of the specified category to the specified level.
     *
     * <p>If the category provided is not present in the current logging configuration, it is created and set to the
     * specified logging level.
     *
     * @throws  NullPointerException      if either category or level are <code>null</code>.
     * @throws  IllegalArgumentException  if the provided logging level is invalid.
     */
    @ManagedOperation(description = "Configures the logging level for the specified category.")
    @Override
    public void setLoggerLevel(final String category, final String level) {
        Preconditions.checkNotNull(category, "Logging category is missing");
        Preconditions.checkNotNull(level, "Logging level is missing");

        /*
         * The result of Level.toLevel(String level) needs to be validated, because when given a wrong logging level it
         * defaults to DEBUG.
         */
        Preconditions.checkArgument(Level.toLevel(level, null) != null, "Invalid logging level: %s", level);

        Logger logger = LogManager.getLogger(category);
        logger.setLevel(Level.toLevel(level));
    }

    /**
     * Returns the current level of the logger associated to the specified category.
     *
     * @param   category  the category which identifies the logger.
     *
     * @return  the current level of the logger, or <code>null</code> if the specified category is not defined.
     *
     * @throws  NullPointerException  if the specified category is <code>null</code>.
     */
    @ManagedOperation(description = "Returns the current logging level for the specified category.")
    @Override
    public String getLoggerLevel(final String category) {
        Preconditions.checkNotNull(category, "Logging category is missing");

        Logger logger = LogManager.getLogger(category);

        // Returns null if there is no such category in the logger configuration
        if (logger == null || logger.getLevel() == null) {
            return null;
        }

        return logger.getLevel().toString();
    }
}
