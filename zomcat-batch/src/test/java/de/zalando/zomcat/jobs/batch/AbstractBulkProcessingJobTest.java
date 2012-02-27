package de.zalando.zomcat.jobs.batch;

import org.junit.Test;

import com.google.common.collect.Lists;

import de.zalando.zomcat.jobs.batch.example.ExampleBulkProcessingJob;
import de.zalando.zomcat.jobs.batch.example.ExampleItem;

import junit.framework.Assert;

/**
 * @author  hjacobs
 */
public class AbstractBulkProcessingJobTest {

    @Test
    public void testExample() {
        ExampleBulkProcessingJob job = new ExampleBulkProcessingJob();
        job.setItems(Lists.newArrayList(new ExampleItem(99)));
        job.doRun(null, null);
        Assert.assertEquals("TEST", job.getItems().get(0).getText());
        Assert.assertEquals(1, job.getSuccessfulItems().size());
        Assert.assertEquals(0, job.getFailedItems().size());
        Assert.assertEquals(99, job.getSuccessfulItems().get(0).getItem().getId());
    }

}
