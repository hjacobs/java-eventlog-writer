package de.zalando.zomcat.jobs.batch.example;

/**
 * @author  hjacobs
 */
public class ExampleItem {

    private long id;
    private String text;

    public ExampleItem(final long id) {
        this.id = id;
    }

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
