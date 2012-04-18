package de.zalando.zomcat.cxf;

import org.apache.cxf.message.Message;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.junit.Before;
import org.junit.Test;

import java.io.PrintWriter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class ExceptionLoggerTest {

    private ExceptionLogger exceptionLogger;
    private Message message;

    @Before
    public void setUp() {
        exceptionLogger = new ExceptionLogger();
        message = createMock(Message.class);
        expect(message.get(AbstractHTTPDestination.HTTP_REQUEST)).andReturn(null);
        replay(message);
    }

    @Test
    public void testShouldNotAcceptNullException() {
        try {
            exceptionLogger.faultOccurred(null, "somewhere in a distant galaxy", null);
            fail("Must raise " + IllegalStateException.class.getName());
        } catch (IllegalStateException ex) {
            // expected behaviour
        }
    }

    @Test
    public void testShouldLogLogEnabledLoggableException() {
        final LoggableExceptionDummy ex = createMock(LoggableExceptionDummy.class);

        expect(ex.isLoggingEnabled()).andReturn(true);
        expectExceptionLogging(ex);
        replay(ex);

        assertFalse(exceptionLogger.faultOccurred(ex, "Just a little bit", message));
    }

    @Test
    public void testShouldLogLogEnabledLoggableExceptionInCause() {
        final LoggableExceptionDummy loggable = createMock(LoggableExceptionDummy.class);
        expect(loggable.isLoggingEnabled()).andReturn(true);
        expectExceptionLogging(loggable);
        replay(loggable);

        final Exception ex = createMock(Exception.class);
        expect(ex.getCause()).andReturn(loggable).anyTimes();
        expect(ex.getStackTrace()).andReturn(new StackTraceElement[0]).anyTimes();
        replay(ex);

        assertFalse(exceptionLogger.faultOccurred(ex, "Just a little bit", message));
    }

    @Test
    public void testShouldNotLogLogDisabledException() {
        final LoggableExceptionDummy ex = createMock(LoggableExceptionDummy.class);

        expect(ex.isLoggingEnabled()).andReturn(false);
        replay(ex);

        assertFalse(exceptionLogger.faultOccurred(ex, "Just a little bit", message));
    }

    @Test
    public void testShouldLogRegularException() {
        final Exception ex = createMock(Exception.class);

        expectExceptionLogging(ex);
        replay(ex);

        assertFalse(exceptionLogger.faultOccurred(ex, "Just a little bit", message));
    }

    private void expectExceptionLogging(Exception ex) {
        expect(ex.getStackTrace()).andReturn(new StackTraceElement[0]);
        expect(ex.getMessage()).andReturn(null);
        expect(ex.getCause()).andReturn(null);
        ex.printStackTrace(anyObject(PrintWriter.class));
        expectLastCall().anyTimes();
    }

    private abstract class LoggableExceptionDummy extends Exception implements Loggable {
    }

}
