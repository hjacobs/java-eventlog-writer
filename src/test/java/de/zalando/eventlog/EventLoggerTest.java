package de.zalando.eventlog;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

import de.zalando.zomcat.flowid.FlowId;

/**
 * @author  hjacobs
 */
public class EventLoggerTest {

    static class EventTypeTest implements EventType {

        @Override
        public List<String> getFieldNames() {
            return Lists.newArrayList("a", "b", "c");
        }

        @Override
        public int getId() {
            return 1;
        }

        @Override
        public String getName() {
            return "TESTEVENT";
        }

    }

    static class EventTypeTest2 implements EventType {

        @Override
        public List<String> getFieldNames() {
            return Lists.newArrayList("d", "e");
        }

        @Override
        public int getId() {
            return 0xae01;
        }

        @Override
        public String getName() {
            return "TESTEVENT2";
        }

    }

    @Test
    public void testLogging() {

        EventLogger logger = EventLogger.getLogger(EventLoggerTest.class);
        logger.log(new EventTypeTest());

        logger.log(new EventTypeTest(), "TESTVAL1", "TAB\tinhere!", "end of row");

        FlowId.generateAndPushFlowId();
        logger.log(new EventTypeTest(), "A", "B");
        logger.log(new EventTypeTest2(), "BLUB", "bla");
    }

}
