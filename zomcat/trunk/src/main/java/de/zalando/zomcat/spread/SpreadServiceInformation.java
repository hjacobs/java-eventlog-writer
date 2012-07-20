package de.zalando.zomcat.spread;

/**
 * @author  hjacobs
 */
public interface SpreadServiceInformation {

    /**
     * @return  the numberOfSendFailures
     */
    long getNumberOfSendFailures();

    /**
     * @return  the lastSuccessfulMessageTime
     */
    Long getLastSuccessfulMessageTime();

    /**
     * @return  the lastFailureMessageTime
     */
    Long getLastFailureMessageTime();
}
