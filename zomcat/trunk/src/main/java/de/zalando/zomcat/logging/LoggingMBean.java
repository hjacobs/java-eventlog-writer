package de.zalando.zomcat.logging;

/**
 * Configuration interface for the logging system. An object of this type can be used to configure logging at runtime.
 *
 * @author  rreis
 */
public interface LoggingMBean {

    /**
     * Sets the logger of the specified category to the specified level.
     *
     * @param  category  the category which identifies the logger.
     * @param  level     the level to set.
     */
    void setLoggerLevel(String category, String level);

    /**
     * Returns the current level of the logger associated to the specified category.
     *
     * @param   category  the category which identifies the logger.
     *
     * @return  the current level of the logger.
     */
    String getLoggerLevel(String category);
}
