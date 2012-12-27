package de.zalando.zomcat.jobs.batch.example.strategy;

import java.io.File;

import org.quartz.JobExecutionContext;

import de.zalando.zomcat.jobs.batch.transition.WriteTime;

public interface FakeJob {
    void setWriteTime(final WriteTime writeTime);

    void setSourceFileName(final String sourceFile);

    void setLogFile(final File logFile);

    void setChunkSize(final int chunkSize);

    /* Needed to simulate executionContext's availability on AbstractJob which is, in runtime, given in by quartz.
     *
     */
    void setExecutionContext(JobExecutionContext dummyExecutionContext);

}
