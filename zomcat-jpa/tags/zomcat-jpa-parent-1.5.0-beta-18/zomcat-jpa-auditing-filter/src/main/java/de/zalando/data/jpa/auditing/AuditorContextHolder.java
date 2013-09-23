package de.zalando.data.jpa.auditing;

/**
 * An AuditorContextHolder very much inspired by Spring-Security.<br/>
 * But should work in our environment too.
 *
 * @author  jbellmann
 */
public class AuditorContextHolder {

    public static final String MODE_THREADLOCAL = "MODE_THREADLOCAL";
    public static final String MODE_INHERITABLETHREADLOCAL = "MODE_INHERITABLETHREADLOCAl";
    public static final String SYSTEM_PROPERTY = "spring.data.auditor.strategy";
    private static String strategyName = System.getProperty(SYSTEM_PROPERTY);
    private static AuditorContextHolderStrategy strategy;
    private static int initializationCount = 0;

    static {
        initialize();
    }

    public static void clearContext() {
        strategy.clearContext();
    }

    public static AuditorContext getContext() {
        return strategy.getContext();
    }

    public static void setContext(final AuditorContext context) {
        strategy.setContext(context);
    }

    public static void setStrategyName(final String strategyName) {
        AuditorContextHolder.strategyName = strategyName;
        initialize();
    }

    public static AuditorContextHolderStrategy getStrategy() {
        return strategy;
    }

    public static AuditorContext createEmptyContext() {
        return strategy.createEmptyContext();
    }

    public static int getInitializationCount() {
        return initializationCount;
    }

    private static void initialize() {
        if (strategyName == null || "".equals(strategyName)) {
            strategyName = MODE_THREADLOCAL;
        }

        if (strategyName.equals(MODE_THREADLOCAL)) {
            strategy = new ThreadLocalAuditorContextHolderStrategy();
        } else if (MODE_INHERITABLETHREADLOCAL.equals(strategyName)) {
            strategy = new InheritableThreadLocalAuditorContextHolderStrategy();
        } else {
            throw new RuntimeException("Not able to create an AuditorContextHolderStrategy");
        }

        initializationCount++;
    }

}
