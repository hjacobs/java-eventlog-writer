package de.zalando.zomcat.jobs.batch.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.math.BigInteger;

import java.util.Date;
import java.util.Random;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;

import org.junit.runner.RunWith;

import org.mockito.Mockito;

import org.quartz.Calendar;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Trigger;

import org.quartz.spi.TriggerFiredBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.ApplicationContext;

import com.google.common.collect.Sets;

import de.zalando.zomcat.jobs.Job;
import de.zalando.zomcat.jobs.JobConfig;
import de.zalando.zomcat.jobs.JobConfigSource;
import de.zalando.zomcat.jobs.JobsStatusBean;
import de.zalando.zomcat.jobs.batch.example.strategy.FakeAllInOneParallelJob;
import de.zalando.zomcat.jobs.batch.example.strategy.FakeChunkedJob;
import de.zalando.zomcat.jobs.batch.example.strategy.FakeItem;
import de.zalando.zomcat.jobs.batch.example.strategy.FakeJob;
import de.zalando.zomcat.jobs.batch.example.strategy.FakeLinearJob;
import de.zalando.zomcat.jobs.batch.transition.AbstractBulkProcessingJob;
import de.zalando.zomcat.jobs.batch.transition.WriteTime;

import junit.framework.Assert;

/**
 * This test builds a file with fake data and dispatches to be processed by bulk jobs. All bulk jobs have the same
 * fetcher, writer and processor. They vary in their strategies and write times.
 *
 * @author  john
 */

class ExpectationSet {
    int qtyCommited;
    int qtyChunks;
    int sumSuccess;
    int sumFail;
    int chunkSize;

    @Override
    public String toString() {
        return String.format(
                "Expectation: \n\tqtyChunks: \t%s\n\tqtyCommited: \t%s\n\tsumSuccess: \t%s\n\tsumFail: \t%s\n\tchunkSize: \t%s",
                qtyChunks, qtyCommited, sumSuccess, sumFail, chunkSize);
    }
}

// dummy class to be able to wire the job execution context without NPEs.
class DummyTrigger extends Trigger {

    @Override
    protected boolean validateMisfireInstruction(final int arg0) {

        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void updateWithNewCalendar(final Calendar arg0, final long arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateAfterMisfire(final Calendar arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void triggered(final Calendar arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setStartTime(final Date arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setEndTime(final Date arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean mayFireAgain() {

        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Date getStartTime() {

        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Date getPreviousFireTime() {

        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Date getNextFireTime() {

        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Date getFireTimeAfter(final Date arg0) {

        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Date getFinalFireTime() {

        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Date getEndTime() {

        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int executionComplete(final JobExecutionContext arg0, final JobExecutionException arg1) {

        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Date computeFirstFireTime(final Calendar arg0) {

        // TODO Auto-generated method stub
        return null;
    }
}

@RunWith(Theories.class)
public class StrategyBulkJobsTest {

    private final Logger LOG = LoggerFactory.getLogger(StrategyBulkJobsTest.class);

    private static final int FILE_LENGTH = 2000;
    protected static final String APP_INSTANCE_KEY = "key";
    private static final int LIMIT = 100;
    private static String sampleFileName;
    private static File resultFile;

    @DataPoints
    public static WriteTime[] writeTimes = {WriteTime.AT_EACH_ITEM, WriteTime.AT_EACH_CHUNK, WriteTime.AT_END_OF_BATCH};

    @DataPoints
    public static Class<?>[] jobs = {FakeLinearJob.class, FakeChunkedJob.class, FakeAllInOneParallelJob.class};

    // size of the chunk to be simulated. there will be FILE_LENGTH / chunkSize chunks.
    @DataPoints
    public static int[] chunkSizes = {10, 100, 1000};

    @BeforeClass
    public static void setup() throws IOException {

        generateSampleFile();

    }

    @Theory
    public void shouldRespectInvariants(final Class<?> jobClass, final WriteTime writeTime, final int chunkSize)
        throws IOException, InstantiationException, IllegalAccessException {

        ExpectationSet expectations = setupExpectations(jobClass, writeTime, chunkSize);

        AbstractBulkProcessingJob<FakeItem> job = (AbstractBulkProcessingJob<FakeItem>) jobClass.newInstance();
        FakeJob j = (FakeJob) job;

        j.setSourceFileName(sampleFileName);
        j.setWriteTime(writeTime);

        resultFile = java.io.File.createTempFile(jobClass.getSimpleName(), "result");
        LOG.info("Saving result for execution in " + resultFile);
        j.setLogFile(resultFile);
        j.setChunkSize(chunkSize);

        final JobConfig config = new JobConfig(Sets.newHashSet(APP_INSTANCE_KEY), LIMIT, LIMIT, true, null);
        JobConfigSource applicationConfig = new JobConfigSource() {
            @Override
            public String getAppInstanceKey() {
                return APP_INSTANCE_KEY;
            }

            @Override
            public JobConfig getJobConfig(final Job job) {
                return config;
            }
        };

        final ApplicationContext applicationContext = Mockito.mock(ApplicationContext.class);
        final JobsStatusBean jobsStatusBean = new JobsStatusBean();
        Mockito.when(applicationContext.getBean("jobsStatusBean")).thenReturn(jobsStatusBean);
        job.setApplicationContext(applicationContext);

        // If no JobExecutionContext is passed there are going to be NPEs under test. This object is always correclty
        // given in by quartz, but here we simply have to make sure it is not null.
        JobExecutionContext dummyExecutionContext = new JobExecutionContext(null,
                new TriggerFiredBundle(new JobDetail(), new DummyTrigger(), null, false, new Date(), null, null, null),
                null);

        j.setExecutionContext(dummyExecutionContext);
        job.doRun(dummyExecutionContext, config);

        checkExpectations(expectations);

    }

    private void checkExpectations(final ExpectationSet expectations) throws IOException {

        FileReader fileReader = new FileReader(resultFile);
        BufferedReader br = new BufferedReader(fileReader);
        String line = null;
        int qtyLines = 0;
        int sumSuccess = 0;
        int sumFailure = 0;
        while ((line = br.readLine()) != null) {

            // there will be one line per chunk
            qtyLines++;

            String[] parts = line.split(" ");
            int success = Integer.parseInt(parts[0]);
            int failed = Integer.parseInt(parts[1]);

            sumSuccess += success;
            sumFailure += failed;
        }

        Assert.assertEquals("wrong total commited items", expectations.qtyCommited, qtyLines);
        Assert.assertEquals("wrong success sum", expectations.sumSuccess, sumSuccess);
        Assert.assertEquals("wrong fail sum", expectations.sumFail, sumFailure);

    }

    private ExpectationSet setupExpectations(final Class<?> jobClass, final WriteTime writeTime, final int chunkSize) {

        ExpectationSet r = new ExpectationSet();
        r.chunkSize = chunkSize;

        // every tenth item will fail.
        int qtyFailures = FILE_LENGTH / 10;

        r.sumSuccess = FILE_LENGTH - qtyFailures;
        r.sumFail = qtyFailures;

        if (writeTime == WriteTime.AT_EACH_CHUNK) {
            if (jobClass == FakeLinearJob.class) {

                // there's no chunking in linear job.
                r.qtyCommited = 1;
                r.chunkSize = FILE_LENGTH;
            } else {
                r.qtyCommited = FILE_LENGTH / chunkSize;
            }
        }

        if (writeTime == WriteTime.AT_EACH_ITEM) {
            r.qtyCommited = FILE_LENGTH;
        }

        if (writeTime == WriteTime.AT_END_OF_BATCH) {
            r.qtyCommited = 1;
            r.chunkSize = FILE_LENGTH;
        }

        r.qtyChunks = FILE_LENGTH / r.chunkSize;

        LOG.info(String.format("Testing: %s %s %s.\n%s", jobClass, writeTime, chunkSize, r));

        return r;
    }

    @AfterClass
    public static void teardown() throws IOException {

        deleteSampleFile();
    }

    private static void generateSampleFile() throws IOException {

        File sampleFile = java.io.File.createTempFile("bulkProcessing", "testFile");
        sampleFileName = sampleFile.getAbsolutePath();
        System.out.println("file generated: " + sampleFile);

        FileWriter fileWriter = new FileWriter(sampleFile);

        Random random = new Random(System.currentTimeMillis());

        for (int i = 0; i < FILE_LENGTH; i++) {
            fileWriter.write(String.format("%s %s %s\n", i, i % 10 == 0 ? 0 : 1, generateRandomString(random)));
        }

        fileWriter.close();

    }

    private static String generateRandomString(final Random r) {
        return new BigInteger(130, 2, r).toString(32);
    }

    private static void deleteSampleFile() throws IOException {
        File sampleFile = new File(sampleFileName);
        sampleFile.delete();

    }

}
