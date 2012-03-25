package de.zalando.zomcat.cxf;

/**
 * This interface should be implemented by exception classes for controlling whether they are to be logged in any way or
 * not. For example if an exception is only used for flow control between two components.
 *
 * @author  teppel
 */
public interface Loggable {

    boolean isLoggingEnabled();

}
