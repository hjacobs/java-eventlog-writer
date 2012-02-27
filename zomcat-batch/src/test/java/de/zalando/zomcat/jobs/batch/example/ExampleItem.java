package de.zalando.zomcat.jobs.batch.example;

import de.zalando.zomcat.jobs.batch.JobItem;

/**
 * @author  hjacobs
 */
public class ExampleItem implements JobItem {

    private long id;
    private String text;

    public ExampleItem(final long id) {
        this.id = id;
    }

    @Override
    public long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }

}
