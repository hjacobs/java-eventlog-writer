package de.zalando.zomcat.jobs.behaviours;

import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.util.time.Duration;

public class KeepAliveBehavior extends AbstractAjaxTimerBehavior {
    private static final long serialVersionUID = 1L;

    public KeepAliveBehavior() {
        super(Duration.seconds(30));
    }

    @Override
    protected void onTimer(final AjaxRequestTarget target) {

        // prevent wicket changing focus
        target.focusComponent(null);
        target.add(target.getPage().get("operationMode"));
        target.add(target.getPage().get("hearbeatMode"));
        target.add(target.getPage().get("form:group:listContainer"));
    }
}
