import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Map;

public class EventsStatisticTest {

    Clock clock;

    @BeforeEach
    void setUp() {
        clock = Clock.fixed(Instant.now(), ZoneId.systemDefault());
    }

    @Test
    public void testEmpty() {
        final EventsStatistic eventsStatistic = new EventsStatisticImpl(clock);

        Assertions.assertEquals(0.0, eventsStatistic.getEventStatisticByName("test"));
    }

    @Test
    public void testInc() {
        final EventsStatistic eventsStatistic = new EventsStatisticImpl(clock);

        eventsStatistic.incEvent("test");

        Assertions.assertEquals(1.0 / 60, eventsStatistic.getEventStatisticByName("test"));
    }

    @Test
    public void testMany() {
        final EventsStatistic eventsStatistic = new EventsStatisticImpl(clock);

        eventsStatistic.incEvent("event1");
        eventsStatistic.incEvent("event2");
        eventsStatistic.incEvent("event1");
        eventsStatistic.incEvent("event1");
        eventsStatistic.incEvent("event1");
        eventsStatistic.incEvent("event1");
        eventsStatistic.incEvent("event3");
        eventsStatistic.incEvent("event2");
        eventsStatistic.incEvent("event2");
        eventsStatistic.incEvent("event3");

        eventsStatistic.printStatistic();
        Assertions.assertEquals(5.0 / 60, eventsStatistic.getEventStatisticByName("event1"));
        Assertions.assertEquals(3.0 / 60, eventsStatistic.getEventStatisticByName("event2"));
        Assertions.assertEquals(2.0 / 60, eventsStatistic.getEventStatisticByName("event3"));
    }

    @Test
    public void testOffset() {
        final MyClock clock = new MyClock(Clock.fixed(Instant.now(), ZoneId.systemDefault()));
        final EventsStatistic eventsStatistic = new EventsStatisticImpl(clock);
        eventsStatistic.incEvent("test");
        clock.offset(Duration.ofHours(1));

        eventsStatistic.printStatistic();
        Assertions.assertEquals(0.0, eventsStatistic.getEventStatisticByName("test"));
    }

    @Test
    public void testOffsetMany() {
        final MyClock clock = new MyClock(Clock.fixed(Instant.now(), ZoneId.systemDefault()));
        final EventsStatistic eventsStatistic = new EventsStatisticImpl(clock);

        eventsStatistic.incEvent("event1");
        clock.offset(Duration.ofMinutes(5));
        eventsStatistic.incEvent("event2");
        clock.offset(Duration.ofMinutes(1));
        eventsStatistic.incEvent("event1");
        clock.offset(Duration.ofMinutes(1));
        eventsStatistic.incEvent("event1");
        clock.offset(Duration.ofMinutes(2));
        eventsStatistic.incEvent("event1");
        clock.offset(Duration.ofMinutes(37));
        eventsStatistic.incEvent("event1");
        clock.offset(Duration.ofMinutes(4));
        eventsStatistic.incEvent("event3");
        clock.offset(Duration.ofMinutes(2));
        eventsStatistic.incEvent("event2");
        clock.offset(Duration.ofMinutes(8));
        eventsStatistic.incEvent("event2");
        clock.offset(Duration.ofMinutes(1));
        eventsStatistic.incEvent("event3");
        clock.offset(Duration.ofMinutes(5));
        final Map<String, Double> expectedMap = Map.of("event1", 3.0 / 60, "event2", 2.0 / 60, "event3", 2.0 / 60);

        eventsStatistic.printStatistic();
        Assertions.assertEquals(expectedMap, eventsStatistic.getAllEventStatistic());
    }

    @Test
    public void testClear() {
        final MyClock clock = new MyClock(Clock.fixed(Instant.now(), ZoneId.systemDefault()));
        final EventsStatistic eventsStatistic = new EventsStatisticImpl(clock);

        eventsStatistic.incEvent("test");
        Assertions.assertEquals(1.0 / 60, eventsStatistic.getEventStatisticByName("test"));
        eventsStatistic.printStatistic();
        eventsStatistic.clear();

        Assertions.assertEquals(Collections.emptyMap(), eventsStatistic.getAllEventStatistic());
    }
}