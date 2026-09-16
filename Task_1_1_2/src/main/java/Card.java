
 /**
  * Представляет игральную карту с мастью, названием и значением.
  */
public class Card {
    public static String[] suits = {
        "Трефы",
        "Пики",
        "Червы",
        "Бубны"
    };

    public static String[] meanings = {
        "Туз", "Двойка", "Тройка", "Четверка", "Пятерка",
        "Шестерка", "Семерка", "Восьмерка", "Девятка", "Десятка",
        "Валет", "Дама", "Король"
    };

    private static int[] ranks = {
        1, 2, 3, 4, 5,
        6, 7, 8, 9, 10,
        10, 10, 10
    };

    private int suitId;
    private int meaningId;

    /**
     * Создает карту по индексам масти и достоинства.
     *
     * @param suitId индекс масти
     * @param meaningId индекс достоинства
     */
    Card(int suitId, int meaningId) {
        this.suitId = suitId;
        this.meaningId = meaningId;
    }

    /**
     * Возвращает количество очков карты.
     *
     * @return значение карты
     */
    public int getRank() {
        return ranks[meaningId];
    }

    /**
     * Возвращает название достоинства карты.
     *
     * @return название достоинства
     */
    public String getMeaning() {
        return meanings[meaningId];
    }

    /**
     * Возвращает название масти карты.
     *
     * @return название масти
     */
    public String getSuit() {
        return suits[suitId];
    }

    /**
     * Возвращает строковое представление карты.
     *
     * @return масть, достоинство и значение карты
     */
    @Override
    public String toString() {
        return getSuit() + " " + getMeaning()
            + " (" + getRank() + ")";
    }
}