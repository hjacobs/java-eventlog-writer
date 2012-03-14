package de.zalando.zomcat.jobs.batch.example.strategy;

public class FakeItem {
    private int id;
    private String text;

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }
}
