/**
 * Представляет игральную карту с мастью, достоинством и значением.
 */
public class Card {
    private final Suit suit;
    private final Rank rank;

    /**
     * Создает карту.
     *
     * @param suit масть карты
     * @param rank достоинство карты
     */
    Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    /**
     * Возвращает количество очков карты.
     *
     * @return значение карты
     */
    public int getRank() {
        return rank.getValue();
    }

    /**
     * Возвращает достоинство карты.
     *
     * @return достоинство карты
     */
    public Rank getRankValue() {
        return rank;
    }

    /**
     * Возвращает название достоинства карты.
     *
     * @return название достоинства
     */
    public String getMeaning() {
        return rank.getName();
    }

    /**
     * Возвращает название масти карты.
     *
     * @return название масти
     */
    public String getSuit() {
        return suit.getName();
    }

    /**
     * Возвращает строковое представление карты.
     *
     * @return масть, достоинство и значение карты
     */
    @Override
    public String toString() {
        return suit + " " + rank + " (" + getRank() + ")";
    }
}
