package de.zalando.zomcat.configuration;

/**
 * @author  akops
 */
public interface AppInstanceKeySource {

    /**
     * Getter for current AppInstance Key.
     *
     * @return  The current AppInstance Key
     */
    String getAppInstanceKey();
}
