package de.zalando.zomcat.jobs.markup.helper;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.repeater.OddEvenItem;
import org.apache.wicket.model.IModel;

import de.zalando.zomcat.jobs.JobMonitorPage;
import de.zalando.zomcat.jobs.JobTypeStatusBean;
import de.zalando.zomcat.jobs.model.JobRow;

public class HoverOddEvenElement extends OddEvenItem<JobRow> {
    private static final long serialVersionUID = 1L;
    private final String CLASS_EVEN = "even";
    private final String CLASS_ODD = "odd";
    private final String JOB_DISABLED = "disabled";

    private final String ON_MOUSE_OVER = "this.className='litupRowOver';";
    private final String ON_MOUSE_OUT_ODD = "this.className='odd';";
    private final String ON_MOUSE_OUT_EVEN = "this.className='even';";

    public HoverOddEvenElement(final String id, final int index, final IModel<JobRow> model) {
        super(id, index, model);
    }

    @Override
    protected void onComponentTag(final ComponentTag tag) {
        super.onComponentTag(tag);

        final JobTypeStatusBean jobTypeStatusBean = ((JobMonitorPage) getPage()).getJobTypeStatusBean(getModel()
                    .getObject().getJobClass());
        if (jobTypeStatusBean.getJobConfig().isActive()) {
            tag.put("class", (getIndex() % 2 == 0) ? CLASS_EVEN : CLASS_ODD);
            tag.put("onmouseover", ON_MOUSE_OVER);
            tag.put("onmouseout", (getIndex() % 2 == 0) ? ON_MOUSE_OUT_EVEN : ON_MOUSE_OUT_ODD);
        } else {
            tag.put("class", JOB_DISABLED);
        }
    }
}
