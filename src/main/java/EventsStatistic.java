import java.util.Map;

public interface EventsStatistic {
    // инкрементит число событий name
    void incEvent(String name);

    // выдает rpm (request per minute) события name за последний час
    double getEventStatisticByName(String name);

    // выдает rpm всех произошедших событий за прошедший час
    Map<String, Double> getAllEventStatistic();

    // выводит в консоль rpm всех произошедших событий
    void printStatistic();

    void clear();
}
