/**
 * Представляет достоинство игральной карты.
 */
public enum Rank {
    ACE("Туз", 1),
    TWO("Двойка", 2),
    THREE("Тройка", 3),
    FOUR("Четверка", 4),
    FIVE("Пятерка", 5),
    SIX("Шестерка", 6),
    SEVEN("Семерка", 7),
    EIGHT("Восьмерка", 8),
    NINE("Девятка", 9),
    TEN("Десятка", 10),
    JACK("Валет", 10),
    QUEEN("Дама", 10),
    KING("Король", 10);

    private final String name;
    private final int value;

    Rank(String name, int value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Возвращает название достоинства карты.
     *
     * @return название достоинства
     */
    public String getName() {
        return name;
    }

    /**
     * Возвращает количество очков карты.
     *
     * @return значение карты
     */
    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return name;
    }
}
