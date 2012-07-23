package de.zalando.zomcat;

/**
 * operation mode of application.
 *
 * @author  fbrick
 */
public enum OperationMode {

    /**
     * NORMAL: normal operation, all jobs are running normally MAINTENANCE: maintenance mode, all jobs finish their
     * operations and then run only idle (they wake up, see it is maintenance mode and start sleeping again)
     */
    NORMAL,
    MAINTENANCE;
}
