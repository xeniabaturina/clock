import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.concurrent.atomic.AtomicReference;

public class MyClock extends Clock {
    private final AtomicReference<Clock> arClock;

    public MyClock(Clock clock) {
        this.arClock = new AtomicReference<>(clock);
    }

    public void offset(Duration offset) {
        arClock.updateAndGet(clock -> Clock.offset(clock, offset));
    }

    @Override
    public ZoneId getZone() {
        return arClock.get().getZone();
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return arClock.get().withZone(zone);
    }

    @Override
    public Instant instant() {
        return arClock.get().instant();
    }
}
