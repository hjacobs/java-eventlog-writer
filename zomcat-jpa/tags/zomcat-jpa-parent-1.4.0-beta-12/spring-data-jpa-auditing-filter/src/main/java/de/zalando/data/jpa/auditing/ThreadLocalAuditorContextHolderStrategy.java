package de.zalando.data.jpa.auditing;

/**
 * Holds an AuditorContext in an {@link ThreadLocal}.
 *
 * @author  jbellmann
 */
final class ThreadLocalAuditorContextHolderStrategy implements AuditorContextHolderStrategy {

    private static final ThreadLocal<AuditorContext> CONTEXTHOLDER = new ThreadLocal<AuditorContext>();

    @Override
    public void setContext(final AuditorContext context) {
        CONTEXTHOLDER.set(context);
    }

    @Override
    public AuditorContext getContext() {
        AuditorContext context = CONTEXTHOLDER.get();
        if (context == null) {
            context = createEmptyContext();
            CONTEXTHOLDER.set(context);
        }

        return context;
    }

    @Override
    public void clearContext() {
        CONTEXTHOLDER.remove();
    }

    @Override
    public AuditorContext createEmptyContext() {
        return new AuditorContextImpl();
    }

}
