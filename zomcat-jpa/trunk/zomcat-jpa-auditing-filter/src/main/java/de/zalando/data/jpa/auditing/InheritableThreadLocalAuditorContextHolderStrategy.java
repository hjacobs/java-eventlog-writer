package de.zalando.data.jpa.auditing;

/**
 * @author  jbellmann
 */
final class InheritableThreadLocalAuditorContextHolderStrategy implements AuditorContextHolderStrategy {

    private static final InheritableThreadLocal<AuditorContext> CONTEXTHOLDER =
        new InheritableThreadLocal<AuditorContext>();

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
