package de.zalando.zomcat;

public enum HeartbeatMode {

    OK("OK: Zalando JVM is running"),
    DEPLOY("Deploy: Zalando JVM is in Updateprocess");

    private String loadbalancerMessage = null;

    private HeartbeatMode(final String loadbalancerMessage) {
        this.loadbalancerMessage = loadbalancerMessage;
    }

    /**
     * @return  the loadbalancerMessage
     */
    public String getLoadbalancerMessage() {
        return loadbalancerMessage;
    }
}
