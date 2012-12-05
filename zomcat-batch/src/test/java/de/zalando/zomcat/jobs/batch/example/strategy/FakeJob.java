package de.zalando.zomcat.jobs.batch.example.strategy;

import java.io.File;

import de.zalando.zomcat.jobs.batch.transition.WriteTime;

public interface FakeJob {
    void setWriteTime(final WriteTime writeTime);

    void setSourceFileName(final String sourceFile);

    void setLogFile(final File logFile);

    void setChunkSize(final int chunkSize);

}
