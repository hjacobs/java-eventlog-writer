package de.zalando.zomcat.jobs.listener;

import org.easymock.Capture;
import org.easymock.EasyMock;

import org.joda.time.DateTime;

import org.junit.Test;

import org.quartz.JobExecutionContext;

import de.zalando.zomcat.jobs.RunningWorker;

public class JobHistoryListenerTest {

    private JobHistoryListener jobHistoryListener = new JobHistoryListener();

    @Test
    public void startRunning() throws Exception {
        final RunningWorker runningWorker = EasyMock.createMock(RunningWorker.class);
        final DateTime dateTime = new DateTime();
        EasyMock.expect(runningWorker.getInternalStartTime()).andReturn(dateTime);
        EasyMock.expect(runningWorker.getId()).andReturn(1);
        runningWorker.setFlowId(EasyMock.anyObject(String.class));
        EasyMock.replay(runningWorker);

        // context unused in history listener
        final JobExecutionContext context = null;
        jobHistoryListener.startRunning(runningWorker, context, "host");
    }

    @Test
    public void stopRunning() throws Exception {
        final RunningWorker runningWorker = EasyMock.createMock(RunningWorker.class);
        final DateTime dateTime = new DateTime();
        EasyMock.expect(runningWorker.getInternalStartTime()).andReturn(dateTime);
        EasyMock.expect(runningWorker.getId()).andReturn(1);

        final Capture<String> captured = new Capture<String>();
        runningWorker.setFlowId(EasyMock.capture(captured));
        EasyMock.replay(runningWorker);

        // context unused in history listener
        final JobExecutionContext context = null;
        jobHistoryListener.startRunning(runningWorker, context, "host");

        EasyMock.reset(runningWorker);
        EasyMock.expect(runningWorker.getFlowId()).andReturn(captured.getValue());
        EasyMock.expect(runningWorker.getId()).andReturn(1);
        EasyMock.replay(runningWorker);

        jobHistoryListener.stopRunning(runningWorker, null);
    }

    @Test
    public void stopRunningWithException() throws Exception {
        final RunningWorker runningWorker = EasyMock.createMock(RunningWorker.class);
        final DateTime dateTime = new DateTime();
        EasyMock.expect(runningWorker.getInternalStartTime()).andReturn(dateTime);
        EasyMock.expect(runningWorker.getId()).andReturn(1);

        final Capture<String> captured = new Capture<String>();
        runningWorker.setFlowId(EasyMock.capture(captured));
        EasyMock.replay(runningWorker);

        // context unused in history listener
        final JobExecutionContext context = null;
        jobHistoryListener.startRunning(runningWorker, context, "host");

        EasyMock.reset(runningWorker);
        EasyMock.expect(runningWorker.getFlowId()).andReturn(captured.getValue());
        EasyMock.expect(runningWorker.getId()).andReturn(1);
        EasyMock.replay(runningWorker);

        jobHistoryListener.stopRunning(runningWorker, new RuntimeException("test"));
    }

    @Test
    public void stopRunningWithoutStartRunning() throws Exception {
        final RunningWorker runningWorker = EasyMock.createMock(RunningWorker.class);
        EasyMock.reset(runningWorker);
        EasyMock.expect(runningWorker.getFlowId()).andReturn("-2");
        EasyMock.expectLastCall().times(2);
        EasyMock.expect(runningWorker.getId()).andReturn(1);
        EasyMock.replay(runningWorker);

        // this will log an exception, but it will not throw anything.
        jobHistoryListener.stopRunning(runningWorker, new RuntimeException("test"));
    }
}
