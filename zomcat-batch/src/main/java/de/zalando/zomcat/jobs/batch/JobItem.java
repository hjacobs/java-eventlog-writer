package de.zalando.zomcat.jobs.batch;

/**
 * Base interface of a logistics item. One item is one work package, which makes sense for the current File Type, ie.:
 * order vs. position based processing.
 *
 * @author  teppel
 */
public interface JobItem {

    long getId();

}
