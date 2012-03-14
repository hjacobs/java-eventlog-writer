package de.zalando.zomcat.jobs.batch.transition;

public enum WriteTime {
    AT_EACH_CHUNK,
    AT_EACH_ITEM,
    AT_END_OF_BATCH
}
