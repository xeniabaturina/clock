import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class EventsStatisticImpl implements EventsStatistic {
    private final Clock clock;
    private final Map<String, List<Instant>> InstantsByEvent = new HashMap<>();

    public EventsStatisticImpl(Clock clock) {
        this.clock = clock;
    }

    @Override
    public void incEvent(String name) {
        final Instant instant = clock.instant();
        InstantsByEvent.putIfAbsent(name, new CopyOnWriteArrayList<>());
        InstantsByEvent.get(name).add(instant);
    }

    private double getEventStatisticFrom(List<Instant> instants, Instant l, Instant r) {
        return instants.stream().filter(x -> x.compareTo(l) > 0 && x.compareTo(r) <= 0).collect(Collectors.collectingAndThen(Collectors.counting(), x -> x / 60.0));
    }

    @Override
    public double getEventStatisticByName(String name) {
        final Instant rightBound = clock.instant();
        final Instant leftBound = rightBound.minus(Duration.ofHours(1));
        final List<Instant> instants = InstantsByEvent.getOrDefault(name, Collections.emptyList());
        return getEventStatisticFrom(instants, leftBound, rightBound);
    }

    @Override
    public Map<String, Double> getAllEventStatistic() {
        final Instant r = clock.instant();
        final Instant l = r.minus(Duration.ofHours(1));
        return InstantsByEvent.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> getEventStatisticFrom(e.getValue(), l, r)));
    }

    @Override
    public void printStatistic() {
        getAllEventStatistic().forEach((key, value) -> System.out.println(key + ": " + value));
    }

    @Override
    public void clear() {
        this.InstantsByEvent.clear();
    }
}